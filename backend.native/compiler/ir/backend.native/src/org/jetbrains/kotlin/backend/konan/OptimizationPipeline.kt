package org.jetbrains.kotlin.backend.konan

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import llvm.*
import org.jetbrains.kotlin.konan.target.KonanTarget

// Initialize all required LLVM machinery, ex. target registry.
private fun initializeLlvm() {
    LLVMKotlinInitialize()

    val passRegistry = LLVMGetGlobalPassRegistry()

    LLVMInitializeCore(passRegistry)
    LLVMInitializeTransformUtils(passRegistry)
    LLVMInitializeScalarOpts(passRegistry)
    LLVMInitializeObjCARCOpts(passRegistry)
    LLVMInitializeVectorization(passRegistry)
    LLVMInitializeInstCombine(passRegistry)
    LLVMInitializeIPO(passRegistry)
    LLVMInitializeInstrumentation(passRegistry)
    LLVMInitializeAnalysis(passRegistry)
    LLVMInitializeIPA(passRegistry)
    LLVMInitializeCodeGen(passRegistry)
    LLVMInitializeTarget(passRegistry)
}

internal fun shouldRunLateBitcodePasses(context: Context): Boolean {
    return context.coverage.enabled
}

internal fun runLateBitcodePasses(context: Context, llvmModule: LLVMModuleRef) {
    val passManager = LLVMCreatePassManager()!!
    val targetLibraryInfo = LLVMGetTargetLibraryInfo(llvmModule)
    LLVMAddTargetLibraryInfo(targetLibraryInfo, passManager)
    context.coverage.addLateLlvmPasses(passManager)
    LLVMRunPassManager(passManager, llvmModule)
    LLVMDisposePassManager(passManager)
}

private class LlvmPipelineConfiguration(context: Context) {

    private val target = context.config.target

    val targetTriple: String = context.llvm.targetTriple

    val cpuArchitecture: String = when (target) {
        KonanTarget.IOS_ARM32 -> "armv7"
        KonanTarget.IOS_ARM64 -> "arm64"
        KonanTarget.LINUX_X64 -> "x86-64"
        KonanTarget.MINGW_X86 -> "sandybridge"
        KonanTarget.MACOS_X64 -> "core2"
        KonanTarget.LINUX_ARM32_HFP -> "arm1136jf-s"
        else -> error("There is no support for ${target.name} target yet")
    }

    val cpuFeatures: String = ""

    val inlineThreshold: Int? = when {
        context.shouldOptimize() -> 100
        context.shouldContainDebugInfo() -> null
        else -> null
    }

    val optimizationLevel: Int = when {
        context.shouldOptimize() -> 3
        context.shouldContainDebugInfo() -> 0
        else -> 1
    }

    val sizeLevel: Int = when {
        context.shouldOptimize() -> 0
        context.shouldContainDebugInfo() -> 0
        else -> 0
    }

    val codegenOptimizationLevel: LLVMCodeGenOptLevel = when {
        context.shouldOptimize() -> LLVMCodeGenOptLevel.LLVMCodeGenLevelAggressive
        context.shouldContainDebugInfo() -> LLVMCodeGenOptLevel.LLVMCodeGenLevelNone
        else -> LLVMCodeGenOptLevel.LLVMCodeGenLevelDefault
    }

    val relocMode: LLVMRelocMode = LLVMRelocMode.LLVMRelocDefault

    val codeModel: LLVMCodeModel = LLVMCodeModel.LLVMCodeModelDefault
}

internal fun runLlvmOptimizationPipeline(context: Context) {
    val llvmModule = context.llvmModule!!
    val config = LlvmPipelineConfiguration(context)

    memScoped {
        initializeLlvm()
        val passBuilder = LLVMPassManagerBuilderCreate()
        val modulePasses = LLVMCreatePassManager()
        LLVMPassManagerBuilderSetOptLevel(passBuilder, config.optimizationLevel)
        LLVMPassManagerBuilderSetSizeLevel(passBuilder, config.sizeLevel)
        // TODO: use LLVMGetTargetFromName instead.
        val target = alloc<LLVMTargetRefVar>()
        if (LLVMGetTargetFromTriple(config.targetTriple, target.ptr, null) != 0) {
            context.reportCompilationError("Cannot get target from triple ${config.targetTriple}.")
        }
        val targetMachine = LLVMCreateTargetMachine(
                target.value,
                config.targetTriple,
                config.cpuArchitecture,
                config.cpuFeatures,
                config.codegenOptimizationLevel,
                config.relocMode,
                config.codeModel)

        val targetLibraryInfo = LLVMGetTargetLibraryInfo(llvmModule)
        LLVMAddTargetLibraryInfo(targetLibraryInfo, modulePasses)
        // TargetTransformInfo pass.
        LLVMAddAnalysisPasses(targetMachine, modulePasses)
        // Since we are in a "closed world" internalization and global dce
        // can be safely used to reduce size of a bitcode.
        LLVMAddInternalizePass(modulePasses, 0)
        LLVMAddGlobalDCEPass(modulePasses)

        config.inlineThreshold?.let { threshold ->
            LLVMPassManagerBuilderUseInlinerWithThreshold(passBuilder, threshold)
        }
        // Pipeline that is similar to `llvm-lto`.
        LLVMPassManagerBuilderPopulateLTOPassManager(passBuilder, modulePasses, Internalize = 0, RunInliner = 1)
        LLVMPassManagerBuilderDispose(passBuilder)

        LLVMRunPassManager(modulePasses, llvmModule)

        LLVMDisposeTargetMachine(targetMachine)
        LLVMDisposePassManager(modulePasses)
    }
    if (shouldRunLateBitcodePasses(context)) {
        runLateBitcodePasses(context, llvmModule)
    }
}
/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

package org.jetbrains.kotlin.backend.konan

import llvm.*
import org.jetbrains.kotlin.backend.konan.llvm.parseBitcodeFile
import org.jetbrains.kotlin.konan.exec.Command
import org.jetbrains.kotlin.konan.target.*

typealias BitcodeFile = String
typealias ObjectFile = String
typealias ExecutableFile = String

private fun mangleSymbol(target: KonanTarget,symbol: String) =
        if (target.family == Family.IOS || target.family == Family.OSX) {
            "_$symbol"
        } else {
            symbol
        }

internal class BitcodeCompiler(val context: Context) {

    private val target = context.config.target
    private val platform = context.config.platform
    private val optimize = context.shouldOptimize()
    private val debug = context.config.debug

    private fun MutableList<String>.addNonEmpty(elements: List<String>) {
        addAll(elements.filter { !it.isEmpty() })
    }

    private val exportedSymbols = context.coverage.addExportedSymbols()

    private fun runTool(vararg command: String) =
            Command(*command)
                    .logWith(context::log)
                    .execute()

    private fun temporary(name: String, suffix: String): String =
            context.config.tempFiles.create(name, suffix).absolutePath

    private fun targetTool(tool: String, vararg arg: String) {
        val absoluteToolName = if (platform.configurables is AppleConfigurables) {
            "${platform.absoluteTargetToolchain}/usr/bin/$tool"
        } else {
            "${platform.absoluteTargetToolchain}/bin/$tool"
        }
        runTool(absoluteToolName, *arg)
    }

    private fun hostLlvmTool(tool: String, vararg arg: String) {
        val absoluteToolName = "${platform.absoluteLlvmHome}/bin/$tool"
        runTool(absoluteToolName, *arg)
    }

    private fun llvmLto(files: List<BitcodeFile>): ObjectFile {
        val configurables = platform.configurables as LlvmLtoFlags
        val combined = temporary("combined", ".o")
        val arguments = mutableListOf<String>().apply {
            addNonEmpty(configurables.llvmLtoFlags)
            addNonEmpty(llvmProfilingFlags())
            when {
                optimize -> addNonEmpty(configurables.llvmLtoOptFlags)
                debug -> addNonEmpty(platform.llvmDebugOptFlags)
                else -> addNonEmpty(configurables.llvmLtoNooptFlags)
            }
            addNonEmpty(configurables.llvmLtoDynamicFlags)
            addNonEmpty(files)
            // Prevent symbols from being deleted by DCE.
            addNonEmpty(exportedSymbols.map { "-exported-symbol=${mangleSymbol(target, it)}"} )
        }
        hostLlvmTool("llvm-lto", "-o", combined, *arguments.toTypedArray())
        return combined
    }

    private fun llvmLink(bitcodeFiles: List<BitcodeFile>): BitcodeFile {
        val combinedBc = temporary("link_output", ".bc")
        hostLlvmTool("llvm-link", *bitcodeFiles.toTypedArray(), "-o", combinedBc)
        return combinedBc
    }

    private fun opt(bitcodeFile: BitcodeFile): BitcodeFile {
        val optFlags = platform.configurables as OptFlags
        val flags = (optFlags.optFlags + when {
            optimize -> optFlags.optOptFlags
            debug -> optFlags.optDebugFlags
            else -> optFlags.optNooptFlags
        } + llvmProfilingFlags()).toTypedArray()
        val optimizedBc = temporary("opt_output", ".bc")
        hostLlvmTool("opt", bitcodeFile, "-o", optimizedBc, *flags)

        if (shouldRunLateBitcodePasses(context)) {
            val module = parseBitcodeFile(optimizedBc)
            runLateBitcodePasses(context, module)
            LLVMWriteBitcodeToFile(module, optimizedBc)
        }

        return optimizedBc
    }

    private fun llc(bitcodeFile: BitcodeFile): ObjectFile {
        val llcFlags = platform.configurables as LlcFlags
        val flags = (llcFlags.llcFlags + when {
            optimize -> llcFlags.llcOptFlags
            debug -> llcFlags.llcDebugFlags
            else -> llcFlags.llcNooptFlags
        } + llvmProfilingFlags()).toTypedArray()
        val combinedO = temporary("llc_output", ".o")
        hostLlvmTool("llc", bitcodeFile, "-o", combinedO, *flags, "-filetype=obj")
        return combinedO
    }

    private fun bitcodeToWasm(bitcodeFiles: List<BitcodeFile>): String {
        val combinedBc = llvmLink(bitcodeFiles)
        val optimizedBc = opt(combinedBc)
        val compiled = llc(optimizedBc)

        // TODO: should be moved to linker.
        val lldFlags = platform.configurables as LldFlags
        val linkedWasm = temporary("linked", ".wasm")
        hostLlvmTool("wasm-ld", compiled, "-o", linkedWasm, *lldFlags.lldFlags.toTypedArray())

        return linkedWasm
    }

    private fun llvmLinkAndLlc(bitcodeFiles: List<BitcodeFile>): String {
        val combinedBc = llvmLink(bitcodeFiles)

        val optimizedBc = temporary("optimized", ".bc")
        val optFlags = llvmProfilingFlags() + listOf("-O3", "-internalize", "-globaldce")
        hostLlvmTool("opt", combinedBc, "-o=$optimizedBc", *optFlags.toTypedArray())

        val combinedO = temporary("combined", ".o")
        val llcFlags = llvmProfilingFlags() + listOf("-function-sections", "-data-sections")
        hostLlvmTool("llc", optimizedBc, "-filetype=obj", "-o", combinedO, *llcFlags.toTypedArray())

        return combinedO
    }

    private fun clang(file: BitcodeFile): ObjectFile {
        val clangFlags = platform.configurables as ClangFlags
        val objectFile = temporary("result", ".o")

        val profilingFlags = llvmProfilingFlags().map { listOf("-mllvm", it) }.flatten()

        val flags = mutableListOf<String>().apply {
            addNonEmpty(clangFlags.clangFlags)
            addNonEmpty(listOf("-triple", context.llvm.targetTriple))
            addNonEmpty(when {
                optimize -> clangFlags.clangOptFlags
                debug -> clangFlags.clangDebugFlags
                else -> clangFlags.clangNooptFlags
            })
            addNonEmpty(BitcodeEmbedding.getClangOptions(context.config))
            if (determineLinkerOutput(context) == LinkerOutputKind.DYNAMIC_LIBRARY) {
                addNonEmpty(clangFlags.clangDynamicFlags)
            }
            addNonEmpty(profilingFlags)
        }
        targetTool("clang++", *flags.toTypedArray(), file, "-o", objectFile)
        return objectFile
    }

    // llvm-lto, opt and llc share same profiling flags, so we can
    // reuse this function.
    private fun llvmProfilingFlags(): List<String> {
        val flags = mutableListOf<String>()
        if (context.shouldProfilePhases()) {
            flags += "-time-passes"
        }
        if (context.inVerbosePhase) {
            flags += "-debug-pass=Structure"
        }
        return flags
    }

    fun makeObjectFiles(bitcodeFile: BitcodeFile): List<ObjectFile> {

        val bitcodeFiles = listOf(bitcodeFile)

        return listOf(when (platform.configurables) {
            is AppleConfigurables ->
                clang(bitcodeFile)
            is WasmConfigurables ->
                bitcodeToWasm(bitcodeFiles)
            is ZephyrConfigurables ->
                llvmLinkAndLlc(bitcodeFiles)
            is LlvmLtoFlags ->
                llvmLto(bitcodeFiles)
            else ->
                error("Unsupported configurables kind: ${platform.configurables::class.simpleName}!")
        })
    }
}
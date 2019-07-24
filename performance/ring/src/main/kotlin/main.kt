/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.jetbrains.ring.*
import octoTest
import org.jetbrains.benchmarksLauncher.*
import org.jetbrains.kliopt.*

class RingLauncher : Launcher() {

    override val benchmarks = BenchmarksCollection(
            mutableMapOf(
                    "AbstractMethod.sortStrings" to BenchmarkEntryWithInit.create(::AbstractMethodBenchmark, { sortStrings() }),
                    "AbstractMethod.sortStringsWithComparator" to BenchmarkEntryWithInit.create(::AbstractMethodBenchmark, { sortStringsWithComparator() }),
                    "ClassArray.copy" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { copy() }),
                    "ClassArray.copyManual" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { copyManual() }),
                    "ClassArray.filterAndCount" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { filterAndCount() }),
                    "ClassArray.filterAndMap" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { filterAndMap() }),
                    "ClassArray.filterAndMapManual" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { filterAndMapManual() }),
                    "ClassArray.filter" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { filter() }),
                    "ClassArray.filterManual" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { filterManual() }),
                    "ClassArray.countFilteredManual" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { countFilteredManual() }),
                    "ClassArray.countFiltered" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { countFiltered() }),
                    "ClassArray.countFilteredLocal" to BenchmarkEntryWithInit.create(::ClassArrayBenchmark, { countFilteredLocal() }),
                    "ClassBaseline.consume" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { consume() }),
                    "ClassBaseline.consumeField" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { consumeField() }),
                    "ClassBaseline.allocateList" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { allocateList() }),
                    "ClassBaseline.allocateArray" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { allocateArray() }),
                    "ClassBaseline.allocateListAndFill" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { allocateListAndFill() }),
                    "ClassBaseline.allocateListAndWrite" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { allocateListAndWrite() }),
                    "ClassBaseline.allocateArrayAndFill" to BenchmarkEntryWithInit.create(::ClassBaselineBenchmark, { allocateArrayAndFill() }),
                    "ClassList.copy" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { copy() }),
                    "ClassList.copyManual" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { copyManual() }),
                    "ClassList.filterAndCount" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndCount() }),
                    "ClassList.filterAndCountWithLambda" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndCountWithLambda() }),
                    "ClassList.filterWithLambda" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterWithLambda() }),
                    "ClassList.mapWithLambda" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { mapWithLambda() }),
                    "ClassList.countWithLambda" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { countWithLambda() }),
                    "ClassList.filterAndMapWithLambda" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndMapWithLambda() }),
                    "ClassList.filterAndMapWithLambdaAsSequence" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndMapWithLambdaAsSequence() }),
                    "ClassList.filterAndMap" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndMap() }),
                    "ClassList.filterAndMapManual" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterAndMapManual() }),
                    "ClassList.filter" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filter() }),
                    "ClassList.filterManual" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { filterManual() }),
                    "ClassList.countFilteredManual" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { countFilteredManual() }),
                    "ClassList.countFiltered" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { countFiltered() }),
                    "ClassList.reduce" to BenchmarkEntryWithInit.create(::ClassListBenchmark, { reduce() }),
                    "ClassStream.copy" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { copy() }),
                    "ClassStream.copyManual" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { copyManual() }),
                    "ClassStream.filterAndCount" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { filterAndCount() }),
                    "ClassStream.filterAndMap" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { filterAndMap() }),
                    "ClassStream.filterAndMapManual" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { filterAndMapManual() }),
                    "ClassStream.filter" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { filter() }),
                    "ClassStream.filterManual" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { filterManual() }),
                    "ClassStream.countFilteredManual" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { countFilteredManual() }),
                    "ClassStream.countFiltered" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { countFiltered() }),
                    "ClassStream.reduce" to BenchmarkEntryWithInit.create(::ClassStreamBenchmark, { reduce() }),
                    "CompanionObject.invokeRegularFunction" to BenchmarkEntryWithInit.create(::CompanionObjectBenchmark, { invokeRegularFunction() }),
                    "CompanionObject.invokeJvmStaticFunction" to BenchmarkEntryWithInit.create(::CompanionObjectBenchmark, { invokeJvmStaticFunction() }),
                    "DefaultArgument.testOneOfTwo" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testOneOfTwo() }),
                    "DefaultArgument.testTwoOfTwo" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testTwoOfTwo() }),
                    "DefaultArgument.testOneOfFour" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testOneOfFour() }),
                    "DefaultArgument.testFourOfFour" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testFourOfFour() }),
                    "DefaultArgument.testOneOfEight" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testOneOfEight() }),
                    "DefaultArgument.testEightOfEight" to BenchmarkEntryWithInit.create(::DefaultArgumentBenchmark, { testEightOfEight() }),
                    "Elvis.testElvis" to BenchmarkEntryWithInit.create(::ElvisBenchmark, { testElvis() }),
                    "Euler.problem1bySequence" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem1bySequence() }),
                    "Euler.problem1" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem1() }),
                    "Euler.problem2" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem2() }),
                    "Euler.problem4" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem4() }),
                    "Euler.problem8" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem8() }),
                    "Euler.problem9" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem9() }),
                    "Euler.problem14" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem14() }),
                    "Euler.problem14full" to BenchmarkEntryWithInit.create(::EulerBenchmark, { problem14full() }),
                    "Fibonacci.calcClassic" to BenchmarkEntryWithInit.create(::FibonacciBenchmark, { calcClassic() }),
                    "Fibonacci.calc" to BenchmarkEntryWithInit.create(::FibonacciBenchmark, { calc() }),
                    "Fibonacci.calcWithProgression" to BenchmarkEntryWithInit.create(::FibonacciBenchmark, { calcWithProgression() }),
                    "Fibonacci.calcSquare" to BenchmarkEntryWithInit.create(::FibonacciBenchmark, { calcSquare() }),
                    "ForLoops.arrayLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { arrayLoop() }),
                    "ForLoops.intArrayLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { intArrayLoop() }),
                    "ForLoops.floatArrayLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { floatArrayLoop() }),
                    "ForLoops.charArrayLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { charArrayLoop() }),
                    "ForLoops.stringLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { stringLoop() }),
                    "ForLoops.arrayIndicesLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { arrayIndicesLoop() }),
                    "ForLoops.intArrayIndicesLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { intArrayIndicesLoop() }),
                    "ForLoops.floatArrayIndicesLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { floatArrayIndicesLoop() }),
                    "ForLoops.charArrayIndicesLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { charArrayIndicesLoop() }),
                    "ForLoops.stringIndicesLoop" to BenchmarkEntryWithInit.create(::ForLoopsBenchmark, { stringIndicesLoop() }),
                    "Inline.calculate" to BenchmarkEntryWithInit.create(::InlineBenchmark, { calculate() }),
                    "Inline.calculateInline" to BenchmarkEntryWithInit.create(::InlineBenchmark, { calculateInline() }),
                    "Inline.calculateGeneric" to BenchmarkEntryWithInit.create(::InlineBenchmark, { calculateGeneric() }),
                    "Inline.calculateGenericInline" to BenchmarkEntryWithInit.create(::InlineBenchmark, { calculateGenericInline() }),
                    "IntArray.copy" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { copy() }),
                    "IntArray.copyManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { copyManual() }),
                    "IntArray.filterAndCount" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterAndCount() }),
                    "IntArray.filterSomeAndCount" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterSomeAndCount() }),
                    "IntArray.filterAndMap" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterAndMap() }),
                    "IntArray.filterAndMapManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterAndMapManual() }),
                    "IntArray.filter" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filter() }),
                    "IntArray.filterSome" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterSome() }),
                    "IntArray.filterPrime" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterPrime() }),
                    "IntArray.filterManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterManual() }),
                    "IntArray.filterSomeManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { filterSomeManual() }),
                    "IntArray.countFilteredManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredManual() }),
                    "IntArray.countFilteredSomeManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredSomeManual() }),
                    "IntArray.countFilteredPrimeManual" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredPrimeManual() }),
                    "IntArray.countFiltered" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFiltered() }),
                    "IntArray.countFilteredSome" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredSome() }),
                    "IntArray.countFilteredPrime" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredPrime() }),
                    "IntArray.countFilteredLocal" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredLocal() }),
                    "IntArray.countFilteredSomeLocal" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { countFilteredSomeLocal() }),
                    "IntArray.reduce" to BenchmarkEntryWithInit.create(::IntArrayBenchmark, { reduce() }),
                    "IntBaseline.consume" to BenchmarkEntryWithInit.create(::IntBaselineBenchmark, { consume() }),
                    "IntBaseline.allocateList" to BenchmarkEntryWithInit.create(::IntBaselineBenchmark, { allocateList() }),
                    "IntBaseline.allocateArray" to BenchmarkEntryWithInit.create(::IntBaselineBenchmark, { allocateArray() }),
                    "IntBaseline.allocateListAndFill" to BenchmarkEntryWithInit.create(::IntBaselineBenchmark, { allocateListAndFill() }),
                    "IntBaseline.allocateArrayAndFill" to BenchmarkEntryWithInit.create(::IntBaselineBenchmark, { allocateArrayAndFill() }),
                    "IntList.copy" to BenchmarkEntryWithInit.create(::IntListBenchmark, { copy() }),
                    "IntList.copyManual" to BenchmarkEntryWithInit.create(::IntListBenchmark, { copyManual() }),
                    "IntList.filterAndCount" to BenchmarkEntryWithInit.create(::IntListBenchmark, { filterAndCount() }),
                    "IntList.filterAndMap" to BenchmarkEntryWithInit.create(::IntListBenchmark, { filterAndMap() }),
                    "IntList.filterAndMapManual" to BenchmarkEntryWithInit.create(::IntListBenchmark, { filterAndMapManual() }),
                    "IntList.filter" to BenchmarkEntryWithInit.create(::IntListBenchmark, { filter() }),
                    "IntList.filterManual" to BenchmarkEntryWithInit.create(::IntListBenchmark, { filterManual() }),
                    "IntList.countFilteredManual" to BenchmarkEntryWithInit.create(::IntListBenchmark, { countFilteredManual() }),
                    "IntList.countFiltered" to BenchmarkEntryWithInit.create(::IntListBenchmark, { countFiltered() }),
                    "IntList.countFilteredLocal" to BenchmarkEntryWithInit.create(::IntListBenchmark, { countFilteredLocal() }),
                    "IntList.reduce" to BenchmarkEntryWithInit.create(::IntListBenchmark, { reduce() }),
                    "IntStream.copy" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { copy() }),
                    "IntStream.copyManual" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { copyManual() }),
                    "IntStream.filterAndCount" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { filterAndCount() }),
                    "IntStream.filterAndMap" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { filterAndMap() }),
                    "IntStream.filterAndMapManual" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { filterAndMapManual() }),
                    "IntStream.filter" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { filter() }),
                    "IntStream.filterManual" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { filterManual() }),
                    "IntStream.countFilteredManual" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { countFilteredManual() }),
                    "IntStream.countFiltered" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { countFiltered() }),
                    "IntStream.countFilteredLocal" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { countFilteredLocal() }),
                    "IntStream.reduce" to BenchmarkEntryWithInit.create(::IntStreamBenchmark, { reduce() }),
                    "Lambda.noncapturingLambda" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { noncapturingLambda() }),
                    "Lambda.noncapturingLambdaNoInline" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { noncapturingLambdaNoInline() }),
                    "Lambda.capturingLambda" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { capturingLambda() }),
                    "Lambda.capturingLambdaNoInline" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { capturingLambdaNoInline() }),
                    "Lambda.mutatingLambda" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { mutatingLambda() }),
                    "Lambda.mutatingLambdaNoInline" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { mutatingLambdaNoInline() }),
                    "Lambda.methodReference" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { methodReference() }),
                    "Lambda.methodReferenceNoInline" to BenchmarkEntryWithInit.create(::LambdaBenchmark, { methodReferenceNoInline() }),
                    "Loop.arrayLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayLoop() }),
                    "Loop.arrayIndexLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayIndexLoop() }),
                    "Loop.rangeLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { rangeLoop() }),
                    "Loop.arrayListLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayListLoop() }),
                    "Loop.arrayWhileLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayWhileLoop() }),
                    "Loop.arrayForeachLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayForeachLoop() }),
                    "Loop.arrayListForeachLoop" to BenchmarkEntryWithInit.create(::LoopBenchmark, { arrayListForeachLoop() }),
                    "MatrixMap.add" to BenchmarkEntryWithInit.create(::MatrixMapBenchmark, { add() }),
                    "ParameterNotNull.invokeOneArgWithNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeOneArgWithNullCheck() }),
                    "ParameterNotNull.invokeOneArgWithoutNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeOneArgWithoutNullCheck() }),
                    "ParameterNotNull.invokeTwoArgsWithNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeTwoArgsWithNullCheck() }),
                    "ParameterNotNull.invokeTwoArgsWithoutNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeTwoArgsWithoutNullCheck() }),
                    "ParameterNotNull.invokeEightArgsWithNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeEightArgsWithNullCheck() }),
                    "ParameterNotNull.invokeEightArgsWithoutNullCheck" to BenchmarkEntryWithInit.create(::ParameterNotNullAssertionBenchmark, { invokeEightArgsWithoutNullCheck() }),
                    "PrimeList.calcDirect" to BenchmarkEntryWithInit.create(::PrimeListBenchmark, { calcDirect() }),
                    "PrimeList.calcEratosthenes" to BenchmarkEntryWithInit.create(::PrimeListBenchmark, { calcEratosthenes() }),
                    "String.stringConcat" to BenchmarkEntryWithInit.create(::StringBenchmark, { stringConcat() }),
                    "String.stringConcatNullable" to BenchmarkEntryWithInit.create(::StringBenchmark, { stringConcatNullable() }),
                    "String.stringBuilderConcat" to BenchmarkEntryWithInit.create(::StringBenchmark, { stringBuilderConcat() }),
                    "String.stringBuilderConcatNullable" to BenchmarkEntryWithInit.create(::StringBenchmark, { stringBuilderConcatNullable() }),
                    "String.summarizeSplittedCsv" to BenchmarkEntryWithInit.create(::StringBenchmark, { summarizeSplittedCsv() }),
                    "Switch.testSparseIntSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testSparseIntSwitch() }),
                    "Switch.testDenseIntSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testDenseIntSwitch() }),
                    "Switch.testConstSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testConstSwitch() }),
                    "Switch.testObjConstSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testObjConstSwitch() }),
                    "Switch.testVarSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testVarSwitch() }),
                    "Switch.testStringsSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testStringsSwitch() }),
                    "Switch.testEnumsSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testEnumsSwitch() }),
                    "Switch.testDenseEnumsSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testDenseEnumsSwitch() }),
                    "Switch.testSealedWhenSwitch" to BenchmarkEntryWithInit.create(::SwitchBenchmark, { testSealedWhenSwitch() }),
                    "WithIndicies.withIndicies" to BenchmarkEntryWithInit.create(::WithIndiciesBenchmark, { withIndicies() }),
                    "WithIndicies.withIndiciesManual" to BenchmarkEntryWithInit.create(::WithIndiciesBenchmark, { withIndiciesManual() }),
                    "OctoTest" to BenchmarkEntry(::octoTest),
                    "Calls.finalMethod" to BenchmarkEntryWithInit.create(::CallsBenchmark, { finalMethodCall() }),
                    "Calls.openMethodMonomorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { classOpenMethodCall_MonomorphicCallsite() }),
                    "Calls.openMethodBimorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { classOpenMethodCall_BimorphicCallsite() }),
                    "Calls.openMethodTrimorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { classOpenMethodCall_TrimorphicCallsite() }),
                    "Calls.interfaceMethodMonomorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { interfaceMethodCall_MonomorphicCallsite() }),
                    "Calls.interfaceMethodBimorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { interfaceMethodCall_BimorphicCallsite() }),
                    "Calls.interfaceMethodTrimorphic" to BenchmarkEntryWithInit.create(::CallsBenchmark, { interfaceMethodCall_TrimorphicCallsite() }),
                    "Calls.returnBoxUnboxFolding" to BenchmarkEntryWithInit.create(::CallsBenchmark, { returnBoxUnboxFolding() }),
                    "Calls.parameterBoxUnboxFolding" to BenchmarkEntryWithInit.create(::CallsBenchmark, { parameterBoxUnboxFolding() }),
                    "CoordinatesSolver.solve" to BenchmarkEntryWithInit.create(::CoordinatesSolverBenchmark, { solve() }),
                    "GraphSolver.solve" to BenchmarkEntryWithInit.create(::GraphSolverBenchmark, { solve() })
            )
    )
}

fun main(args: Array<String>) {
    val launcher = RingLauncher()
    BenchmarksRunner.runBenchmarks(args, { arguments: BenchmarkArguments ->
        if (arguments is BaseBenchmarkArguments) {
            launcher.launch(arguments.warmup, arguments.repeat, arguments.prefix,
                    arguments.filter, arguments.filterRegex)
        } else emptyList()
    }, benchmarksListAction = launcher::benchmarksListAction)
}
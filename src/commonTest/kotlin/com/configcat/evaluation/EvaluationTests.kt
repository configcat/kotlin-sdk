package com.configcat.evaluation

import com.configcat.ConfigCatClient
import com.configcat.SingleValueCache
import com.configcat.evaluation.data.*
import com.configcat.log.LogLevel
import com.configcat.manualPoll
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluationTests {

    @Test
    fun testSimpleValue() = runTest {
        testEvaluation(SimpleValueTests)
    }

    @Test
    fun testOneTargetRule() = runTest {
        testEvaluation(OneTargetingRuleTests)
    }

    @Test
    fun testTwoTargetingRules() = runTest {
        testEvaluation(TwoTargetingRulesTests)
    }

    @Test
    fun testAndRules() = runTest {
        testEvaluation(AndRulesTests)
    }

    @Test
    fun testComparators() = runTest {
        // Native test run separately for this test
        if (PlatformUtils.IS_NATIVE) {
            return@runTest
        }
        testEvaluation(ComparatorsTests)
    }

    @Test
    fun testSemverValidation() = runTest {
        testEvaluation(SemverValidationTests)
    }

    @Test
    fun testNumberValidation() = runTest {
        testEvaluation(NumberValidationTests)
    }

    @Test
    fun testEpochDateValidation() = runTest {
        // Native test run separately for this test
        if (PlatformUtils.IS_NATIVE) {
            return@runTest
        }
        testEvaluation(EpochDateValidationTests)
    }

    @Test
    fun testPrerequisiteFlag() = runTest {
        testEvaluation(PrerequisiteFlagTests)
    }

    @Test
    fun testSegment() = runTest {
        testEvaluation(SegmenTests)
    }

    @Test
    fun testOptionsAfterTargetingRule() = runTest {
        testEvaluation(OptionsAfterTargetingRuleTests)
    }

    @Test
    fun testOptionsBasedOnUserId() = runTest {
        testEvaluation(OptionsBasedOnUserIdTests)
    }

    @Test
    fun testOptionsBasedOnCustomAttr() = runTest {
        testEvaluation(OptionsBasedOnCustomAttrTests)
    }

    @Test
    fun testOptionsWithinTargetingRule() = runTest {
        testEvaluation(OptionsWithinTargetingRuleTests)
    }

    @Test
    fun testListTruncation() = runTest {
        testEvaluation(ListTruncationTests)
    }

    private suspend fun testEvaluation(testSet: TestSet) {
        var sdkKey = testSet.sdkKey
        if (sdkKey.isNullOrEmpty()) {
            sdkKey = TEST_SDK_KEY
        }

        val mockEngine = MockEngine {
            respond(
                content = testSet.jsonOverride,
                status = HttpStatusCode.OK,
                headersOf(Pair("ETag", listOf("fakeETag")))
            )
        }

        val evaluationTestLogger = EvaluationTestLogger()
        val client = ConfigCatClient(sdkKey) {
            pollingMode = manualPoll()
            baseUrl = testSet.baseUrl
            httpEngine = mockEngine
            logger = evaluationTestLogger
            logLevel = LogLevel.INFO

            // add empty SingleValueCache to avoid JS extra cache logs
            configCache = SingleValueCache("")
        }
        client.forceRefresh()

        val tests = testSet.tests
        val errors: ArrayList<String> = arrayListOf()
        for (test in tests!!) {
            val settingKey = test.key

            val result: Any = client.getAnyValue(settingKey, test.defaultValue, test.user)
            if (test.returnValue != result) {
                errors.add("Return value mismatch for test: %s Test Key: $settingKey Expected: ${test.returnValue}, Result: $result \n")
            }
            val expectedLog = test.expectedLog
            val logResultBuilder = StringBuilder()
            val logsList = evaluationTestLogger.getLogList()
            for (i in logsList.indices) {
                val log = logsList[i]
                logResultBuilder.append(log.logMessage)
                if (i != logsList.size - 1) {
                    logResultBuilder.append("\n")
                }
            }
            val logResult: String = logResultBuilder.toString()
            if (expectedLog != logResult) {
                errors.add("Log mismatch for test: %s Test Key: $settingKey Expected:\n$expectedLog\nResult:\n$logResult\n")
            }
            evaluationTestLogger.resetLogList()
        }
        if (errors.isNotEmpty()) {
            println("\n == ERRORS == \n")
            fail(errors.joinToString("\n"))
        }
        client.close()
    }

    companion object {
        private const val TEST_SDK_KEY = "configcat-sdk-test-key/0000000000000000000000"
    }
}

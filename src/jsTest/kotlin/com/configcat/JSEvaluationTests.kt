package com.configcat

import com.configcat.data.JSComparatorsTests
import com.configcat.data.JSEpochDateValidationTests
import com.configcat.evaluation.EvaluationTestLogger
import com.configcat.evaluation.data.*
import com.configcat.log.LogLevel
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

/**
 * Run the Evaluation test cases where double format used. This tests cases has a different expected value.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class JSEvaluationTests {

    @Test
    fun testComparators() = runTest {
        // The test contains formatted double value, which is different in case of JS module
        testEvaluation(JSComparatorsTests)
    }

    @Test
    fun testEpochDateValidation() = runTest {
        // The test contains formatted double value, which is different in case of JS module
        testEvaluation(JSEpochDateValidationTests)
    }

    private suspend fun testEvaluation(testSet: TestSet) {
        var sdkKey = testSet.sdkKey
        if (sdkKey.isNullOrEmpty()) {
            sdkKey = TEST_SDK_KEY
        }

        var mockEngine = MockEngine {
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
        }
        client.forceRefresh()

        val tests = testSet.tests
        var errors: ArrayList<String> = arrayListOf()
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
                var log = logsList[i]
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

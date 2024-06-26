
import com.configcat.ConfigCatClient
import com.configcat.SingleValueCache
import com.configcat.data.DarwinComparatorsTests
import com.configcat.data.DarwinEpochDateValidationTests
import com.configcat.evaluation.EvaluationTestLogger
import com.configcat.evaluation.data.TestSet
import com.configcat.log.LogLevel
import com.configcat.manualPoll
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

class DarwinEvaluationTests {
    @Test
    fun testComparators() =
        runTest {
            // The test contains formatted double value, which is different in case of JS module
            testEvaluation(DarwinComparatorsTests)
        }

    @Test
    fun testEpochDateValidation() =
        runTest {
            // The test contains formatted double value, which is different in case of JS module
            testEvaluation(DarwinEpochDateValidationTests)
        }

    private suspend fun testEvaluation(testSet: TestSet) {
        var sdkKey = testSet.sdkKey
        if (sdkKey.isNullOrEmpty()) {
            sdkKey = TEST_SDK_KEY
        }

        val mockEngine =
            MockEngine {
                respond(
                    content = testSet.jsonOverride,
                    status = HttpStatusCode.OK,
                    headersOf(Pair("ETag", listOf("fakeETag"))),
                )
            }

        val evaluationTestLogger = EvaluationTestLogger()
        val client =
            ConfigCatClient(sdkKey) {
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

            val result: Any? = client.getAnyValue(settingKey, test.defaultValue, test.user)
            if (test.returnValue != result) {
                errors.add(
                    "Return value mismatch for test: %s Test Key: " +
                        "$settingKey Expected: ${test.returnValue}, Result: $result \n",
                )
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
                errors.add(
                    "Log mismatch for test: %s Test Key: $settingKey Expected:\n$expectedLog\nResult:\n$logResult\n",
                )
            }
            evaluationTestLogger.resetLogList()
        }

        client.close()

        if (errors.isNotEmpty()) {
            fail(errors.joinToString("\n"))
        }
    }

    companion object {
        private const val TEST_SDK_KEY = "configcat-sdk-test-key/0000000000000000000000"
    }
}

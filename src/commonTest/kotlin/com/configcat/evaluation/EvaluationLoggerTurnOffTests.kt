package com.configcat.evaluation

import com.configcat.ConfigCatClient
import com.configcat.log.LogLevel
import com.configcat.manualPoll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

// Test cases based on EvaluationTest 1_rule_no_user test case.
@OptIn(ExperimentalCoroutinesApi::class)
class EvaluationLoggerTurnOffTest {
    @Test
    fun testEvaluationLogLevelInfo() = runTest {
        //based on 1_rule_no_user test case.
        val evaluationTestLogger = EvaluationTestLogger()
        val client = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A") {
            pollingMode = manualPoll()
            logger = evaluationTestLogger
            logLevel = LogLevel.INFO
        }
        client.forceRefresh()

        val result: Any = client.getAnyValue("stringContainsDogDefaultCat", "default", null)

        val logList = evaluationTestLogger.getLogList()
        assertEquals("Cat", result, "Return value not match.")
        assertEquals(2, evaluationTestLogger.getLogList().size, "Logged event size not match.")
        assertEquals(LogLevel.WARNING, logList[0].logLevel, "Logged event level not match.")
        assertEquals(LogLevel.INFO, logList[1].logLevel, "Logged event level not match.")

        client.close()
    }

    @Test
    fun testEvaluationLogLevelWarning() = runTest {
        //based on 1_rule_no_user test case.
        val evaluationTestLogger = EvaluationTestLogger()
        val client = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A") {
            pollingMode = manualPoll()
            logger = evaluationTestLogger
            logLevel = LogLevel.WARNING
        }
        client.forceRefresh()

        val result: Any = client.getAnyValue("stringContainsDogDefaultCat", "default", null)

        val logList = evaluationTestLogger.getLogList()
        assertEquals("Cat", result, "Return value not match.")
        assertEquals(1, logList.size, "Logged event size not match.")
        assertEquals(LogLevel.WARNING, logList[0].logLevel, "Logged event level not match.")

        client.close()
    }
}
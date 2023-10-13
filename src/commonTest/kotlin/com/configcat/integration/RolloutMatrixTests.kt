package com.configcat.integration

import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValueDetails
import com.configcat.integration.matrix.*
import com.configcat.manualPoll
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.fail

@OptIn(ExperimentalCoroutinesApi::class)
class RolloutMatrixTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testMatrix() = runTest {
        runMatrixTest(Matrix,true)
    }

    @Test
    fun testNumberMatrix() = runTest {
        runMatrixTest(NumberMatrix, true)
    }

    @Test
    fun testSemanticMatrix() = runTest {
        runMatrixTest(SemanticMatrix, true)
    }

    @Test
    fun testSemantic2Matrix() = runTest {
        runMatrixTest(SemanticMatrix2, true)
    }

    @Test
    fun testSensitiveMatrix() = runTest {
        runMatrixTest(SensitiveMatrix, true)
    }

    @Test
    fun testVariationMatrix() = runTest {
        runMatrixTest(VariationIdMatrix, false)
    }

    @Test
    fun testAndOrMatrix() = runTest {
        runMatrixTest(AndOrMatrix, true)
    }

    @Test
    fun testComparatorsV6Matrix() = runTest {
        runMatrixTest(ComparatorsV6Matrix, true)
    }

    @Test
    fun testPrerequisiteFlagMatrix() = runTest {
        runMatrixTest(PrerequisiteFlagMatrix, true)
    }

    @Test
    fun testSegmentMatrix() = runTest {
        runMatrixTest(SegmentMatrix, true)
    }

    private suspend fun runMatrixTest(matrix: DataMatrix, isValueKind: Boolean) {
        val mockEngine = MockEngine {
            respond(content = matrix.remoteJson, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(matrix.sdkKey) {
            pollingMode = manualPoll()
            httpEngine = mockEngine
        }
        client.forceRefresh()

        val rows = matrix.data.lines()
        val header = rows[0].split(";")
        val customKey = header[3]
        val settingKeys = header.drop(4)
        val errors = mutableListOf<String>()
        for (i in 1 until rows.size) {
            val testObjects = rows[i].split(";")
            var user: ConfigCatUser? = null
            if (testObjects[0] != "##null##") {
                var email = ""
                var country = ""

                val identifier = testObjects[0]
                if (testObjects[1].isNotEmpty() && testObjects[1] != "##null##") {
                    email = testObjects[1]
                }

                if (testObjects[2].isNotEmpty() && testObjects[2] != "##null##") {
                    country = testObjects[2]
                }

                val custom = mutableMapOf<String, String>()
                if (testObjects[3].isNotEmpty() && testObjects[3] != "##null##") {
                    custom[customKey] = testObjects[3]
                }

                user = ConfigCatUser(identifier, email, country, custom)
            }

            for ((j, settingKey) in settingKeys.withIndex()) {
                if (isValueKind) {
                    val value = client.getAnyValue(settingKey, "", user)
                    val boolVal = value as? Boolean
                    if (boolVal != null) {
                        val expected = testObjects[j + 4].lowercase().toBooleanStrictOrNull()
                        if (boolVal != expected) {
                            errors.add("Identifier: ${testObjects[0]}, Key: $settingKey. UV: ${testObjects[3]} Expected: $expected, Result: $value")
                        }
                        continue
                    }
                    val doubleVal = value as? Double
                    if (doubleVal != null) {
                        val expected = testObjects[j + 4].toDoubleOrNull()
                        if (doubleVal != expected) {
                            errors.add("Identifier: ${testObjects[0]}, Key: $settingKey. UV: ${testObjects[3]} Expected: $expected, Result: $value")
                        }
                        continue
                    }
                    val intVal = value as? Int
                    if (intVal != null) {
                        val expected = testObjects[j + 4].toIntOrNull()
                        if (intVal != expected) {
                            errors.add("Identifier: ${testObjects[0]}, Key: $settingKey. UV: ${testObjects[3]} Expected: $expected, Result: $value")
                        }
                        continue
                    }

                    val expected = testObjects[j + 4]
                    if (value != expected) {
                        errors.add("Identifier: ${testObjects[0]}, Key: $settingKey. UV: ${testObjects[3]} Expected: $expected, Result: $value")
                    }
                } else {
                    val variationId = client.getValueDetails(settingKey, "", user).variationId
                    if (variationId != testObjects[j + 4]) {
                        errors.add("Identifier: ${testObjects[0]}, Key: $settingKey. UV: ${testObjects[3]} Expected: ${testObjects[j + 4]}, Result: $variationId")
                    }
                }
            }
        }

        if (errors.size > 0) {
            fail(errors.joinToString("\n"))
        }
    }
}

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
        Matrix.sdkKeyV5?.let { runMatrixTest(Matrix, it,true) }
        Matrix.sdkKeyV6?.let { runMatrixTest(Matrix, it,true) }
    }

    @Test
    fun testNumberMatrix() = runTest {
        Matrix.sdkKeyV5?.let { runMatrixTest(NumberMatrix, it,true) }
        Matrix.sdkKeyV6?.let { runMatrixTest(NumberMatrix, it,true) }
    }

    @Test
    fun testSemanticMatrix() = runTest {
        Matrix.sdkKeyV5?.let { runMatrixTest(SemanticMatrix, it,true) }
        Matrix.sdkKeyV6?.let { runMatrixTest(SemanticMatrix, it,true) }
    }

    @Test
    fun testSemantic2Matrix() = runTest {
        Matrix.sdkKeyV5?.let { runMatrixTest(SemanticMatrix2, it,true) }
        Matrix.sdkKeyV6?.let { runMatrixTest(SemanticMatrix2, it,true) }
    }

    @Test
    fun testSensitiveMatrix() = runTest {
        Matrix.sdkKeyV5?.let { runMatrixTest(SensitiveMatrix, it,true) }
        Matrix.sdkKeyV6?.let { runMatrixTest(SensitiveMatrix, it,true) }
    }

    @Test
    fun testVariationMatrix() = runTest {
        Matrix.sdkKeyV5?.let { runMatrixTest(VariationIdMatrix, it,false) }
        Matrix.sdkKeyV6?.let { runMatrixTest(VariationIdMatrix, it,false) }
    }

    @Test
    fun testAndOrMatrix() = runTest {
        Matrix.sdkKeyV6?.let { runMatrixTest(AndOrMatrix, it,true) }
    }

    @Test
    fun testComparatorsV6Matrix() = runTest {
        Matrix.sdkKeyV6?.let { runMatrixTest(ComparatorsV6Matrix, it,true) }
    }

    @Test
    fun testPrerequisiteFlagMatrix() = runTest {
        Matrix.sdkKeyV6?.let { runMatrixTest(PrerequisiteFlagMatrix, it,true) }
    }

    @Test
    fun testSegmentMatrix() = runTest {
        Matrix.sdkKeyV6?.let { runMatrixTest(SegmentMatrix, it,true) }
    }

    private suspend fun runMatrixTest(matrix: DataMatrix, sdkyKey: String, isValueKind: Boolean) {
        val mockEngine = MockEngine {
            respond(content = matrix.remoteJson, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(sdkyKey) {
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

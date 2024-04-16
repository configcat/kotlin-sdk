package com.configcat

import com.configcat.model.Condition
import com.configcat.model.Config
import com.configcat.model.PercentageOption
import com.configcat.model.Preferences
import com.configcat.model.ServedValue
import com.configcat.model.Setting
import com.configcat.model.SettingValue
import com.configcat.model.TargetingRule
import com.configcat.model.UserCondition
import com.configcat.override.OverrideBehavior
import com.configcat.override.OverrideDataSource
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class OverrideTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testLocalOnly() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithBoolean(false), status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(Data.SDK_KEY) {
                    httpEngine = mockEngine
                    flagOverrides = {
                        behavior = OverrideBehavior.LOCAL_ONLY
                        dataSource =
                            OverrideDataSource.map(
                                mapOf(
                                    "enabledFeature" to true,
                                    "disabledFeature" to false,
                                    "intSetting" to 5,
                                    "doubleSetting" to 3.14,
                                    "stringSetting" to "test",
                                ),
                            )
                    }
                }

            assertEquals(true, client.getValue("enabledFeature", false))
            assertEquals(false, client.getValue("disabledFeature", true))
            assertEquals(5, client.getValue("intSetting", 0))
            assertEquals(3.14, client.getValue("doubleSetting", 0.0))
            assertEquals("test", client.getValue("stringSetting", ""))
            assertEquals(0, mockEngine.requestHistory.size)
        }

    @Test
    fun testLocalOverRemote() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithBoolean(false), status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(Data.SDK_KEY) {
                    httpEngine = mockEngine
                    flagOverrides = {
                        behavior = OverrideBehavior.LOCAL_OVER_REMOTE
                        dataSource =
                            OverrideDataSource.map(
                                mapOf(
                                    "fakeKey" to true,
                                    "nonexisting" to true,
                                ),
                            )
                    }
                }

            assertEquals(true, client.getValue("fakeKey", false))
            assertEquals(true, client.getValue("nonexisting", false))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testRemoteOverLocal() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithBoolean(false), status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(Data.SDK_KEY) {
                    httpEngine = mockEngine
                    flagOverrides = {
                        behavior = OverrideBehavior.REMOTE_OVER_LOCAL
                        dataSource =
                            OverrideDataSource.map(
                                mapOf(
                                    "fakeKey" to true,
                                    "nonexisting" to true,
                                ),
                            )
                    }
                }

            assertEquals(false, client.getValue("fakeKey", true))
            assertEquals(true, client.getValue("nonexisting", false))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testSettingOverride() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithBoolean(false), status = HttpStatusCode.OK)
                }

            val user = ConfigCatUser("test@test1.com")

            val client =
                ConfigCatClient(Data.SDK_KEY) {
                    httpEngine = mockEngine
                    flagOverrides = {
                        behavior = OverrideBehavior.LOCAL_ONLY
                        dataSource =
                            OverrideDataSource.config(
                                config =
                                    Config(
                                        preferences = Preferences(baseUrl = "test", salt = "test-salt"),
                                        settings =
                                            mapOf(
                                                "noRuleOverride" to
                                                    Setting(
                                                        1,
                                                        "",
                                                        null,
                                                        null,
                                                        SettingValue(stringValue = "noRule"),
                                                        "myVariationId",
                                                    ),
                                                "ruleOverride" to
                                                    Setting(
                                                        1,
                                                        "",
                                                        null,
                                                        arrayOf(
                                                            TargetingRule(
                                                                conditions =
                                                                    arrayOf(
                                                                        Condition(
                                                                            UserCondition(
                                                                                "Identifier",
                                                                                2,
                                                                                stringArrayValue = arrayOf("@test1"),
                                                                            ),
                                                                            null,
                                                                            null,
                                                                        ),
                                                                    ),
                                                                null,
                                                                ServedValue(
                                                                    SettingValue(stringValue = "ruleMatch"),
                                                                    "ruleVariationId",
                                                                ),
                                                            ),
                                                        ),
                                                        SettingValue(stringValue = "noMatch"),
                                                        "myVariationId",
                                                    ),
                                                "percentageOverride" to
                                                    Setting(
                                                        1,
                                                        null,
                                                        arrayOf(
                                                            PercentageOption(75, SettingValue(stringValue = "A"), "percentageAVariationID"),
                                                            PercentageOption(25, SettingValue(stringValue = "B"), "percentageAVariationID"),
                                                        ),
                                                        emptyArray(),
                                                        SettingValue(stringValue = "noMatch"),
                                                        "myVariationId",
                                                    ),
                                            ),
                                    ),
                            )
                    }
                }

            assertEquals("noRule", client.getValue("noRuleOverride", ""))
            assertEquals("ruleMatch", client.getValue("ruleOverride", "", user))
            assertEquals("B", client.getValue("percentageOverride", "", user))
            assertEquals(0, mockEngine.requestHistory.size)
        }
}

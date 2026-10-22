package com.configcat.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ModelsEqualsAndHashCodeTests {
    @Test
    fun configEqualsAndHashCode() {
        val first =
            Config(
                Preferences("https://cdn.example.com", salt = "salt"),
                mapOf("flag" to Setting(settingValue = SettingValue(booleanValue = true))),
                arrayOf(Segment("beta", arrayOf(UserCondition("Identifier", stringValue = "user-1")))),
            )
        val equal =
            Config(
                Preferences("https://cdn.example.com", salt = "salt"),
                mapOf("flag" to Setting(settingValue = SettingValue(booleanValue = true))),
                arrayOf(Segment("beta", arrayOf(UserCondition("Identifier", stringValue = "user-1")))),
            )

        assertTrue(first.equals(equal))
        assertEquals(first.hashCode(), equal.hashCode())
        assertFalse(first.equals(Config(null)))
    }

    @Test
    fun segmentEqualsAndHashCode() {
        val first = Segment("beta", arrayOf(UserCondition("Identifier", stringValue = "user-1")))
        val equal = Segment("beta", arrayOf(UserCondition("Identifier", stringValue = "user-1")))

        assertTrue(first.equals(equal))
        assertEquals(first.hashCode(), equal.hashCode())
        assertFalse(first.equals(Segment("production")))
    }

    @Test
    fun settingEqualsAndHashCode() {
        val first =
            Setting(
                type = 0,
                percentageOptions = arrayOf(PercentageOption(100, SettingValue(booleanValue = true), "variation")),
                settingValue = SettingValue(booleanValue = false),
                variationId = "default",
            )
        val equal =
            Setting(
                type = 0,
                percentageOptions = arrayOf(PercentageOption(100, SettingValue(booleanValue = true), "variation")),
                settingValue = SettingValue(booleanValue = false),
                variationId = "default",
            )

        assertTrue(first.equals(equal))
        assertEquals(first.hashCode(), equal.hashCode())
        assertFalse(
            first.equals(
                Setting(type = 1, settingValue = SettingValue(booleanValue = false), variationId = "default"),
            ),
        )
    }

    @Test
    fun targetingRuleEqualsAndHashCode() {
        val first =
            TargetingRule(
                conditions = arrayOf(Condition(userCondition = UserCondition("Identifier", stringValue = "user-1"))),
                percentageOptions = arrayOf(PercentageOption(100, SettingValue(stringValue = "on"))),
                servedValue = ServedValue(SettingValue(stringValue = "off"), "variation"),
            )
        val equal =
            TargetingRule(
                conditions = arrayOf(Condition(userCondition = UserCondition("Identifier", stringValue = "user-1"))),
                percentageOptions = arrayOf(PercentageOption(100, SettingValue(stringValue = "on"))),
                servedValue = ServedValue(SettingValue(stringValue = "off"), "variation"),
            )

        assertTrue(first.equals(equal))
        assertEquals(first.hashCode(), equal.hashCode())
        assertFalse(
            first.equals(
                TargetingRule(servedValue = ServedValue(SettingValue(stringValue = "different"))),
            ),
        )
    }

    @Test
    fun userConditionEqualsAndHashCode() {
        val first =
            UserCondition(
                "Identifier",
                comparator = 2,
                stringValue = "user-1",
                stringArrayValue = arrayOf("a", "b"),
            )
        val equal =
            UserCondition(
                "Identifier",
                comparator = 2,
                stringValue = "user-1",
                stringArrayValue = arrayOf("a", "b"),
            )

        assertTrue(first.equals(equal))
        assertEquals(first.hashCode(), equal.hashCode())
        assertFalse(
            first.equals(
                UserCondition(
                    "Identifier",
                    comparator = 2,
                    stringValue = "different",
                    stringArrayValue = arrayOf("a", "b"),
                ),
            ),
        )
    }
}
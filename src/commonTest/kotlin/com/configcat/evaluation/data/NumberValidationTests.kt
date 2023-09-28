package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object NumberValidationTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/uGyK3q9_ckmdxRyI7vjwCw"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "number",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "not_a_number") ),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 != '5') for setting 'number' ('not_a_number' is not a valid decimal number). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'number' for User '{"Identifier":"12345","Custom1":"not_a_number"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 != '5' THEN '<>5' => cannot evaluate, the User.Custom1 attribute is invalid ('not_a_number' is not a valid decimal number)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'.
""".trimIndent(),
        )
    )
}
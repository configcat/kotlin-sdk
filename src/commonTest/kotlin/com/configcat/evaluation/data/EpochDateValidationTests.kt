package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object EpochDateValidationTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/OfQqcTjfFUGBwMKqtyEOrQ"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "boolTrueIn202304",
            defaultValue = true,
            returnValue = false,
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "2023.04.10") ),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 AFTER '1680307200' (2023-04-01T00:00:00.000Z UTC)) for setting 'boolTrueIn202304' ('2023.04.10' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'boolTrueIn202304' for User '{"Identifier":"12345","Custom1":"2023.04.10"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 AFTER '1680307200' (2023-04-01T00:00:00.000Z UTC) => false, skipping the remaining AND conditions
    THEN 'true' => cannot evaluate, the User.Custom1 attribute is invalid ('2023.04.10' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch))
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'false'.
""".trimIndent(),
        )
    )
}
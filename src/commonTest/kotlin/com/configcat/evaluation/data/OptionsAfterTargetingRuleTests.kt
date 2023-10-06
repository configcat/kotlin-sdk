package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OptionsAfterTargetingRuleTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "integer25One25Two25Three25FourAdvancedRules",
            defaultValue = 42,
            returnValue = -1,
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'integer25One25Two25Three25FourAdvancedRules' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'integer25One25Two25Three25FourAdvancedRules'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN '5' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Skipping % options because the User Object is missing.
  Returning '-1'.""".trimIndent(),
        ),
        TestCase(
            key = "integer25One25Two25Three25FourAdvancedRules",
            defaultValue = 42,
            returnValue = 2,
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email CONTAINS ANY OF ['@configcat.com']) for setting 'integer25One25Two25Three25FourAdvancedRules' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'integer25One25Two25Three25FourAdvancedRules' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN '5' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Evaluating % options based on the User.Identifier attribute:
  - Computing hash in the [0..99] range from User.Identifier => 25 (this value is sticky and consistent across all SDKs)
  - Hash value 25 selects % option 2 (25%), '2'.
  Returning '2'.""".trimIndent(),
        ),
        TestCase(
            key = "integer25One25Two25Three25FourAdvancedRules",
            defaultValue = 42,
            returnValue = 2,
            user = ConfigCatUser("12345", "joe@example.com"),
            expectedLog = """INFO [5000] Evaluating 'integer25One25Two25Three25FourAdvancedRules' for User '{"Identifier":"12345","Email":"joe@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN '5' => no match
  Evaluating % options based on the User.Identifier attribute:
  - Computing hash in the [0..99] range from User.Identifier => 25 (this value is sticky and consistent across all SDKs)
  - Hash value 25 selects % option 2 (25%), '2'.
  Returning '2'.""".trimIndent(),
        ),
        TestCase(
            key = "integer25One25Two25Three25FourAdvancedRules",
            defaultValue = 42,
            returnValue = 5,
            user = ConfigCatUser("12345", "joe@configcat.com"),
            expectedLog = """INFO [5000] Evaluating 'integer25One25Two25Three25FourAdvancedRules' for User '{"Identifier":"12345","Email":"joe@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN '5' => MATCH, applying rule
  Returning '5'.""".trimIndent(),
        ),
    )
}
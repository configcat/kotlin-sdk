package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OptionsWithinTargetingRuleTests : TestSet {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/x0tcrFMkl02A65D8GD20Eg"
    override val baseUrl = "https://test-cdn-eu.configcat.com"
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email CONTAINS ANY OF ['@configcat.com']) for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@example.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => no match
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@configcat.com"),
            expectedLog = """WARNING [3003] Cannot evaluate % options for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (the User.Country attribute is missing). You should set the User.Country attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => MATCH, applying rule
    Skipping % options because the User.Country attribute is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@configcat.com", "US"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com","Country":"US"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => MATCH, applying rule
    Evaluating % options based on the User.Country attribute:
    - Computing hash in the [0..99] range from User.Country => 63 (this value is sticky and consistent across all SDKs)
    - Hash value 63 selects % option 1 (75%), 'Cat'.
  Returning 'Cat'.
""".trimIndent(),
        ),
    )
}
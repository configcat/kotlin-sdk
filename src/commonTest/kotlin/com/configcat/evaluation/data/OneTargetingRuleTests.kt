package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OneTargetingRuleTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'stringContainsDogDefaultCat' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsDogDefaultCat'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
            user = null
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email CONTAINS ANY OF ['@configcat.com']) for setting 'stringContainsDogDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@example.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345","Email":"joe@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => no match
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Dog",
            user = ConfigCatUser("12345", "joe@configcat.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => MATCH, applying rule
  Returning 'Dog'.
""".trimIndent(),
        ),
    )
}
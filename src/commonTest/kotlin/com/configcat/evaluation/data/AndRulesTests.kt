package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object AndRulesTests: TestSet {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/FfwncdJg1kq0lBqxhYC_7g"
    override val baseUrl = "https://test-cdn-eu.configcat.com"
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "emailAnd",
            defaultValue = "default",
            returnValue = "Cat",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'emailAnd' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'emailAnd'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email STARTS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.""".trimIndent(),
        ),
        TestCase(
            key = "emailAnd",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345","jane@configcat.com"),
            expectedLog = """INFO [5000] Evaluating 'emailAnd' for User '{"Identifier":"12345","Email":"jane@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email STARTS WITH ANY OF [<1 hashed value>] => true
    AND User.Email CONTAINS ANY OF ['@'] => true
    AND User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN 'Dog' => no match
  Returning 'Cat'.""".trimIndent(),
        ),
    )
}
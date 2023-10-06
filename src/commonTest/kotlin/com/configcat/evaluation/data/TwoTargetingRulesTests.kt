package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object TwoTargetingRulesTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "stringIsInDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'stringIsInDogDefaultCat' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringIsInDogDefaultCat'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS ONE OF [<1 hashed value>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringIsInDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email IS ONE OF [<2 hashed values>]) for setting 'stringIsInDogDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3003] Cannot evaluate condition (User.Custom1 IS ONE OF [<1 hashed value>]) for setting 'stringIsInDogDefaultCat' (the User.Custom1 attribute is missing). You should set the User.Custom1 attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringIsInDogDefaultCat' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS ONE OF [<1 hashed value>] THEN 'Dog' => cannot evaluate, the User.Custom1 attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.""".trimIndent(),
        ),
        TestCase(
            key = "stringIsInDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "user")),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email IS ONE OF [<2 hashed values>]) for setting 'stringIsInDogDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringIsInDogDefaultCat' for User '{"Identifier":"12345","Custom1":"user"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS ONE OF [<1 hashed value>] THEN 'Dog' => no match
  Returning 'Cat'.
""".trimIndent(),
        ),
        TestCase(
            key = "stringIsInDogDefaultCat",
            defaultValue = "default",
            returnValue = "Dog",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "admin")),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email IS ONE OF [<2 hashed values>]) for setting 'stringIsInDogDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringIsInDogDefaultCat' for User '{"Identifier":"12345","Custom1":"admin"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS ONE OF [<1 hashed value>] THEN 'Dog' => MATCH, applying rule
  Returning 'Dog'.""".trimIndent(),
        ),

        )
}
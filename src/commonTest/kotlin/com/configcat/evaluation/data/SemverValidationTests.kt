package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object SemverValidationTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/BAr3KgLTP0ObzKnBTo5nhA"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "isNotOneOf",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "wrong_semver")),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 IS NOT ONE OF ['1.0.0', '1.0.1', '2.0.0', '2.0.1', '2.0.2', '']) for setting 'isNotOneOf' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 IS NOT ONE OF ['1.0.0', '3.0.1']) for setting 'isNotOneOf' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'isNotOneOf' for User '{"Identifier":"12345","Custom1":"wrong_semver"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 IS NOT ONE OF ['1.0.0', '1.0.1', '2.0.0', '2.0.1', '2.0.2', ''] THEN 'Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS NOT ONE OF ['1.0.0', '3.0.1'] THEN 'Is not one of (1.0.0, 3.0.1)' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'.
""".trimIndent(),
        ),
        TestCase(
            key = "relations",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "wrong_semver")),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 < '1.0.0,') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 < '1.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 <= '1.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 > '2.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 >= '2.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'relations' for User '{"Identifier":"12345","Custom1":"wrong_semver"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 < '1.0.0,' THEN '<1.0.0,' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 < '1.0.0' THEN '< 1.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 <= '1.0.0' THEN '<=1.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 > '2.0.0' THEN '>2.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 >= '2.0.0' THEN '>=2.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'.""".trimIndent(),
        )
    )
}
package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object SegmenTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/LcYz135LE0qbcacz2mgXnA"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "featureWithSegmentTargeting",
            defaultValue = false,
            returnValue = false,
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'featureWithSegmentTargeting' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'featureWithSegmentTargeting'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS IN SEGMENT 'Beta users' THEN 'true' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'false'.""".trimIndent(),
        ),
        TestCase(
            key = "featureWithSegmentTargeting",
            defaultValue = false,
            returnValue = true,
            user = ConfigCatUser("12345", "jane@example.com"),
            expectedLog = """INFO [5000] Evaluating 'featureWithSegmentTargeting' for User '{"Identifier":"12345","Email":"jane@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS IN SEGMENT 'Beta users'
    (
      Evaluating segment 'Beta users':
      - IF User.Email IS ONE OF [<2 hashed values>] => true
      Segment evaluation result: User IS IN SEGMENT.
      Condition (User IS IN SEGMENT 'Beta users') evaluates to true.
    )
    THEN 'true' => MATCH, applying rule
  Returning 'true'.""".trimIndent(),
        ),
        TestCase(
            key = "featureWithNegatedSegmentTargeting",
            defaultValue = false,
            returnValue = false,
            user = ConfigCatUser("12345", "jane@example.com"),
            expectedLog = """INFO [5000] Evaluating 'featureWithNegatedSegmentTargeting' for User '{"Identifier":"12345","Email":"jane@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS NOT IN SEGMENT 'Beta users'
    (
      Evaluating segment 'Beta users':
      - IF User.Email IS ONE OF [<2 hashed values>] => true
      Segment evaluation result: User IS IN SEGMENT.
      Condition (User IS NOT IN SEGMENT 'Beta users') evaluates to false.
    )
    THEN 'true' => no match
  Returning 'false'.""".trimIndent(),
        ),
    )
}
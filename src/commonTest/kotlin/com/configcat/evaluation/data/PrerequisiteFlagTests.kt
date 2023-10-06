package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object PrerequisiteFlagTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/ByMO9yZNn02kXcm72lnY1A"
    override val baseUrl = null
    override val jsonOverride = null
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "dependentFeatureWithUserCondition",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeatureWithUserCondition'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeatureWithoutUserCondition' EQUALS 'true'
    (
      Evaluating prerequisite flag 'mainFeatureWithoutUserCondition':
      Prerequisite flag evaluation result: 'true'.
      Condition (Flag 'mainFeatureWithoutUserCondition' EQUALS 'true') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Skipping % options because the User Object is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Chicken'.
""".trimIndent(),
        ),
        TestCase(
            key = "dependentFeature",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeature'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'mainFeature' EQUALS 'target'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'target') evaluates to false.
    )
    THEN % options => no match
  Returning 'Chicken'.
""".trimIndent(),
        ),
        TestCase(
            key = "dependentFeatureWithUserCondition2",
            defaultValue = "default",
            returnValue = "Frog",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition2' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeatureWithUserCondition2'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeature' EQUALS 'public'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'public') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Skipping % options because the User Object is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeature' EQUALS 'public'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'public') evaluates to true.
    )
    THEN 'Frog' => MATCH, applying rule
  Returning 'Frog'.
""".trimIndent(),
        ),
        TestCase(
            key = "dependentFeature",
            defaultValue = "default",
            returnValue = "Horse",
            user = ConfigCatUser("12345","kate@configcat.com", "USA"),
            expectedLog = """INFO [5000] Evaluating 'dependentFeature' for User '{"Identifier":"12345","Email":"kate@configcat.com","Country":"USA"}'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'mainFeature' EQUALS 'target'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => no match
      - IF User.Country IS ONE OF [<1 hashed value>] => true
        AND User IS NOT IN SEGMENT 'Beta Users'
        (
          Evaluating segment 'Beta Users':
          - IF User.Email IS ONE OF [<2 hashed values>] => false, skipping the remaining AND conditions
          Segment evaluation result: User IS NOT IN SEGMENT.
          Condition (User IS NOT IN SEGMENT 'Beta Users') evaluates to true.
        ) => true
        AND User IS NOT IN SEGMENT 'Developers'
        (
          Evaluating segment 'Developers':
          - IF User.Email IS ONE OF [<2 hashed values>] => false, skipping the remaining AND conditions
          Segment evaluation result: User IS NOT IN SEGMENT.
          Condition (User IS NOT IN SEGMENT 'Developers') evaluates to true.
        ) => true
        THEN 'target' => MATCH, applying rule
      Prerequisite flag evaluation result: 'target'.
      Condition (Flag 'mainFeature' EQUALS 'target') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Evaluating % options based on the User.Identifier attribute:
    - Computing hash in the [0..99] range from User.Identifier => 78 (this value is sticky and consistent across all SDKs)
    - Hash value 78 selects % option 4 (25%), 'Horse'.
  Returning 'Horse'.
""".trimIndent(),
        ),
    )
}
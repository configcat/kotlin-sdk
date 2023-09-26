package com.configcat.evaluation

import com.configcat.ConfigCatClient
import com.configcat.evaluation.data.SimpleValueTests
import com.configcat.evaluation.data.TestSet
import com.configcat.manualPoll
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.util.logging.*
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.fail

class EvaluationTest {

    //TODO solve the parameterized problem
//        ["simple_value", "1_targeting_rule", "2_targeting_rules", "and_rules", "semver_validation", "epoch_date_validation", "number_validation", "comparators", "circular_dependency",  //TODO fix override
//            "prerequisite_flag", "segment", "options_after_targeting_rule", "options_based_on_user_id", "options_based_on_custom_attr", "options_within_targeting_rule", "list_truncation"]
//

    @Test
    fun testSimpleValue() = runTest {
     testEvaluation(SimpleValueTests)
    }

    private suspend fun testEvaluation(testSet: TestSet) {
//        val testDescriptorContent = readFile(EVALUATION_FOLDER + testDescriptorName + JSON_EXTENSION)
//        val testSet: TestSet = GSON.fromJson(
//            testDescriptorContent,
//            TestSet::class.java
//        )
        var sdkKey = testSet.sdkKey
        if (sdkKey.isNullOrEmpty()) {
            sdkKey = TEST_SDK_KEY
        }
        val jsonOverride = testSet.jsonOverride
        var baseUrl: String?
//        val server: MockWebServer
//        if (jsonOverride != null && !jsonOverride.isEmpty()) {
//            server = MockWebServer()
//            server.start()
//            //override baseUrl in case of mockup
//            baseUrl = server.url("/").toString()
//            val overrideContent = readFile(EVALUATION_FOLDER + testDescriptorName + "/" + jsonOverride)
//            server.enqueue(MockResponse().setResponseCode(200).setBody(overrideContent))
//        } else {
//            baseUrl = testSet.baseUrl
//        }
        //TODO mock if needed
//        val mockEngine = MockEngine {
//            respond(content = matrix.remoteJson, status = HttpStatusCode.OK)
//        }
        val client = ConfigCatClient(sdkKey) {
            pollingMode = manualPoll()
            baseUrl = testSet.baseUrl
            //httpEngine = mockEngine
        }
        client.forceRefresh()

        val tests = testSet.tests
        var errors: List<String> = arrayListOf()
        for (test in tests!!) {
            val settingKey = test.key
//            val clientLogger: Logger = org.slf4j.LoggerFactory.getLogger(ConfigCatClient::class.java) as Logger
             var logger = KotlinLogging.logger {}




//            // create and start a ListAppender
//            clientLogger.setLevel(Level.INFO)
//            val listAppender: ListAppender<ILoggingEvent> = ListAppender<ILoggingEvent>()
//            listAppender.start()
//            clientLogger.addAppender(listAppender)
//            var typeOfExpectedResult: java.lang.Class
//            if (settingKey!!.startsWith("int") || settingKey.startsWith("whole") || settingKey.startsWith("mainInt")) {
//                typeOfExpectedResult = Int::class.java
//            } else if (settingKey.startsWith("double") || settingKey.startsWith("decimal") || settingKey.startsWith("mainDouble")) {
//                typeOfExpectedResult = Double::class.java
//            } else if (settingKey.startsWith("boolean") || settingKey.startsWith("bool") || settingKey.startsWith("mainBool") || settingKey == "developerAndBetaUserSegment" || settingKey == "featureWithSegmentTargeting" || settingKey == "featureWithNegatedSegmentTargeting") {
//                typeOfExpectedResult = Boolean::class.java
//            } else {
//                //handle as String in any other case
//                typeOfExpectedResult = String::class.java
//            }

            val result: Any = client.getAnyValue(settingKey, test.defaultValue, test.user)
            if ( test.returnValue != result) {
                errors.plus("Return value mismatch for test: %s Test Key: $settingKey Expected: ${test.returnValue}, Result: $result \n")
            }
            val expectedLog = test.expectedLog
//            val logResultBuilder: java.lang.StringBuilder = java.lang.StringBuilder()
//            val logsList: List<ILoggingEvent> = listAppender.list
//            for (logEvent in logsList) {
//                logResultBuilder.append(formatLogLevel(logEvent.getLevel())).append(" ")
//                    .append(logEvent.getFormattedMessage()).append("\n")
//            }
//            val logResult: String = logResultBuilder.toString()
            val logResult: String = "logResultBuilder.toString()"
            if (expectedLog != logResult) {
                errors.plus("Log mismatch for test: %s Test Key: $settingKey Expected:\n$expectedLog\nResult:\n$logResult\n")
            }
        }
        if (errors.isNotEmpty()) {
            println("\n == ERRORS == \n")
            fail(errors.joinToString("\n"))
        }
        client.close()
    }

//    private fun convertJsonObjectToUser(jsonObject: com.google.gson.JsonObject?): User? {
//        var user: User? = null
//        if (jsonObject != null) {
//            var email = ""
//            var country = ""
//            val keySet: MutableSet<String> = jsonObject.keySet()
//            val identifier: String = jsonObject.get("Identifier").getAsString()
//            keySet.remove("Identifier")
//            if (jsonObject.has("Email")) {
//                email = jsonObject.get("Email").getAsString()
//                keySet.remove("Email")
//            }
//            if (jsonObject.has("Country")) {
//                country = jsonObject.get("Country").getAsString()
//                keySet.remove("Country")
//            }
//            val customAttributes: MutableMap<String, String> = java.util.HashMap<String, String>()
//            keySet.forEach(java.util.function.Consumer<String> { key: String ->
//                customAttributes.put(
//                    key,
//                    jsonObject.get(key).getAsString()
//                )
//            })
//            user = User.newBuilder()
//                .email(email)
//                .country(country)
//                .custom(customAttributes)
//                .build(identifier)
//        }
//        return user
//    }
//
//    private fun parseObject(classOfT: java.lang.Class<*>, element: com.google.gson.JsonElement?): Any {
//        return if (classOfT == String::class.java) element.getAsString() else if (classOfT == Int::class.java || classOfT == Int::class.javaPrimitiveType) element.getAsInt() else if (classOfT == Double::class.java || classOfT == Double::class.javaPrimitiveType) element.getAsDouble() else if (classOfT == Boolean::class.java || classOfT == Boolean::class.javaPrimitiveType) element.getAsBoolean() else throw java.lang.IllegalArgumentException(
//            "Only String, Integer, Double or Boolean types are supported"
//        )
//    }
//
//
//    private fun readFile(filePath: String): String {
//
//        //TODO read file should work based on the platform
//        //return this::class.java.getResource("/html/file.html").readText()
//         //return File(filePath).inputStream().readBytes().toString(Charsets.UTF_8)
//    }
//
    companion object {
        private const val EVALUATION_FOLDER = "evaluation/"
        private const val TEST_SDK_KEY = "configcat-sdk-test-key/0000000000000000000000"
        private const val JSON_EXTENSION = ".json"

//        private fun formatLogLevel(level: Level): String {
//            if (Level.INFO_INT === level.levelInt) {
//                return "INFO"
//            }
//            if (Level.ERROR_INT === level.levelInt) {
//                return "ERROR"
//            }
//            return if (Level.WARN_INT === level.levelInt) {
//                "WARNING"
//            } else "DEBUG"
//        }
    }
}

//@Serializable
//class TestSet (
//    @SerialName(value = "skdKey")
//    val sdkKey: String? = null,
//
//    @SerialName(value = "baseUrl")
//    val baseUrl: String? = null,
//
//    @SerialName(value = "jsonOverride")
//    val jsonOverride: String? = null,
//
//    @SerialName(value = "tests")
//    val tests: Array<TestCase>? = null
//)
//
//@Serializable
//internal class TestCase (
//    @SerialName(value = "key")
//    val key: String? = null,
//
//    @Contextual
//    @SerialName(value = "returnValue")
//    val returnValue: Any? = null,
//
//    @Contextual
//    @SerialName(value = "defaultValue")
//    val defaultValue: Any?,
//
//    @SerialName(value = "expectedLog")
//    val expectedLog: String? = null,
//
//    @Contextual
//    @SerialName(value = "user")
//    val user: Any? = null,
//)

//internal object FlagValueSerializer : KSerializer<Any> {
//    override fun deserialize(decoder: Decoder): Any {
//        val json = decoder as? JsonDecoder
//            ?: error("Only JsonDecoder is supported.")
//        val element = json.decodeJsonElement()
//        val primitive = element as? JsonPrimitive ?: error("Unable to decode $element")
//        return when (primitive.content) {
//            "true", "false" -> primitive.content == "true"
//            else -> primitive.content.toIntOrNull() ?: primitive.content.toDoubleOrNull() ?: primitive.content
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: Any) {
//        val json = encoder as? JsonEncoder
//            ?: error("Only JsonEncoder is supported.")
//        val element: JsonElement = when (value) {
//            is String -> JsonPrimitive(value)
//            is Number -> JsonPrimitive(value)
//            is Boolean -> JsonPrimitive(value)
//            is JsonElement -> value
//            else -> throw IllegalArgumentException("Unable to encode $value")
//        }
//        json.encodeJsonElement(element)
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    override val descriptor: SerialDescriptor =
//        ContextualSerializer(Any::class, null, emptyArray()).descriptor
//}
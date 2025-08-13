package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.model.Config
import com.configcat.model.SettingType
import com.configcat.model.SettingValue
import org.kotlincrypto.hash.sha1.SHA1
import org.kotlincrypto.hash.sha2.SHA256
import kotlin.time.Instant

internal fun String.parseConfigJson(): Config {
    val config: Config = Constants.json.decodeFromString(this)
    config.addConfigSaltAndSegmentsToSettings()
    return config
}

internal fun Config.addConfigSaltAndSegmentsToSettings() {
    val configSalt = this.preferences?.salt
    this.settings?.values?.forEach {
        it.configSalt = configSalt
        it.segments = this.segments ?: arrayOf()
    }
}

internal fun String.isValidDate(): Boolean {
    this.toLongOrNull() ?: return false
    return true
}

internal fun Double.toDateTimeUTCString(): String {
    val dateInMillisecond: Long = this.toLong() * 1000
    val instant = Instant.fromEpochMilliseconds(dateInMillisecond)
    return instant.toString()
}

internal fun SettingValue?.validateType(
    settingType: Int,
): Any {
    val settingTypeEnum = settingType.toSettingTypeOrNull()
    if (this == null) {
        throw InvalidConfigModelException("Setting value is missing or invalid.")
    }
    val result =
        when (settingTypeEnum) {
            SettingType.BOOLEAN -> {
                this.booleanValue
            }

            SettingType.STRING -> {
                this.stringValue
            }

            SettingType.INT -> {
                this.integerValue
            }

            SettingType.DOUBLE -> {
                this.doubleValue
            }

            SettingType.JS_NUMBER -> {
                this.doubleValue
            }

            else -> {
                throw InvalidConfigModelException(
                    "Setting is of an unsupported type ($settingTypeEnum).",
                )
            }
        }
    if (result == null) {
        throw InvalidConfigModelException("Setting value is not of the expected type ${settingTypeEnum.value}.")
    }
    return result
}

@OptIn(ExperimentalStdlibApi::class)
internal fun ByteArray.sha1Hex(): String = SHA1().digest(this).toHexString()

@OptIn(ExperimentalStdlibApi::class)
internal fun ByteArray.sha256Hex(): String = SHA256().digest(this).toHexString()
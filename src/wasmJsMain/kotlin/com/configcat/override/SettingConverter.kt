package com.configcat.override

import com.configcat.model.Setting

internal actual fun convertToSetting(value: Any): Setting = commonConvertToSetting(value)

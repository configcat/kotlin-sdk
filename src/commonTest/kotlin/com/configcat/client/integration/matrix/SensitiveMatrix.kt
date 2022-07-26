package com.configcat.client.integration.matrix

object SensitiveMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/qX3TP2dTj06ZpCCT1h_SPA"
    override val data = """Identifier;Email;Country;Custom1;isOneOfSensitive;isNotOneOfSensitive
##null##;;;;ToAll;ToAll
id1;macska@example.com;;;Macska;Kigyo
Kutya;;;;Allat;ToAll
Sas;;;;ToAll;Kigyo
Kutya;macska@example.com;;;Macska;ToAll
id1;;Scotland;;Britt;Kigyo
Macska;;USA;;ToAll;Ireland"""
}

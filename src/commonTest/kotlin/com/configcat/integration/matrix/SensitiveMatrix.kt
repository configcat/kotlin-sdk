package com.configcat.integration.matrix

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
    override val remoteJson =
        """{"p":{"u":"https://cdn-global.configcat.com","r":0},"f":{"isNotOneOfSensitive":{"v":"ToAll","t":1,"i":"97bd663d","p":[],"r":[{"o":0,"a":"Identifier","t":17,"c":"68d93aa74a0aa1664f65ad6c0515f24769b15c84,8409e4e5d27a1465165012b03b2606f0e5b08250","v":"Kigyo","i":"4e4356b4"},{"o":1,"a":"Email","t":17,"c":"2e1c7263a639cf2719f585dfa0be3953c13dd36f,532df0aa59af3cf1d3d876316225e987e63bf8a6","v":"Angolna","i":"d75ea4a4"},{"o":2,"a":"Country","t":17,"c":"707fe00aa123eb0be5010f1d3065c2b6d7934ca4,ff95dc990b9440c8ff18edd8592bf43915e510b9,e2ff49d5209adefb1d572ca4ca42701ac5b167ad","v":"Ireland","i":"e8826a82"}]},"isOneOfSensitive":{"v":"ToAll","t":1,"i":"71a78b2a","p":[],"r":[{"o":0,"a":"Email","t":16,"c":"532df0aa59af3cf1d3d876316225e987e63bf8a6","v":"Macska","i":"b1dc4d99"},{"o":1,"a":"Identifier","t":16,"c":"cc1a672b80f85ec48aa620a588864285e2b04a45,68d93aa74a0aa1664f65ad6c0515f24769b15c84","v":"Allat","i":"fb7be8fd"},{"o":2,"a":"Country","t":16,"c":"707fe00aa123eb0be5010f1d3065c2b6d7934ca4,ff95dc990b9440c8ff18edd8592bf43915e510b9,e2ff49d5209adefb1d572ca4ca42701ac5b167ad","v":"Britt","i":"f1b9ed25"}]}}}"""
}

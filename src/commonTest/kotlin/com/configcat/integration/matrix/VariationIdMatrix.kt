package com.configcat.integration.matrix

object VariationIdMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/nQ5qkhRAUEa6beEyyrVLBA"
    override val data = """Identifier;Email;Country;Custom1;boolean;decimal;text;whole
##null##;;;;a0e56eda;63612d39;3f05be89;cf2e9162;
a@configcat.com;a@configcat.com;Hungary;admin;67787ae4;8f9559cf;9bdc6a1f;ab30533b;
b@configcat.com;b@configcat.com;Hungary;admin;67787ae4;8f9559cf;9bdc6a1f;ab30533b;
a@test.com;a@test.com;Hungary;admin;67787ae4;d66c5781;65310deb;ec14f6a9;
b@test.com;b@test.com;Hungary;admin;a0e56eda;d66c5781;65310deb;ec14f6a9;
cliffordj@aol.com;cliffordj@aol.com;Hungary;admin;67787ae4;8155ad7b;cf19e913;ec14f6a9;
bryanw@verizon.net;bryanw@verizon.net;Hungary;;a0e56eda;d0dbc27f;30ba32b9;61a5a033;"""
    override val remoteJson =
        """{"p":{"u":"https://cdn-global.configcat.com","r":0},"f":{"boolean":{"v":false,"i":"a0e56eda","t":0,"p":[{"o":0,"v":true,"p":50,"i":"67787ae4"},{"o":1,"v":false,"p":50,"i":"a0e56eda"}],"r":[{"o":0,"a":"Email","t":2,"c":"@configcat.com","v":true,"i":"67787ae4"}]},"text":{"v":"c","t":1,"i":"3f05be89","p":[{"o":0,"v":"a","p":50,"i":"30ba32b9"},{"o":1,"v":"b","p":50,"i":"cf19e913"}],"r":[{"o":0,"a":"Email","t":2,"c":"@configcat.com","v":"true","i":"9bdc6a1f"},{"o":1,"a":"Email","t":2,"c":"@test.com","v":"false","i":"65310deb"}]},"whole":{"v":999999,"i":"cf2e9162","t":2,"p":[{"o":0,"v":0,"p":50,"i":"ec14f6a9"},{"o":1,"v":-1,"p":50,"i":"61a5a033"}],"r":[{"o":0,"a":"Email","t":2,"c":"@configcat.com","v":1,"i":"ab30533b"}]},"decimal":{"v":0.0,"i":"63612d39","t":3,"p":[{"o":0,"v":1.0,"p":50,"i":"d0dbc27f"},{"o":1,"v":2.0,"p":50,"i":"8155ad7b"}],"r":[{"o":0,"a":"Email","t":2,"c":"@configcat.com","v":-2147483647.2147484,"i":"8f9559cf"},{"o":1,"a":"Email","t":0,"c":"a@test.com","v":0.12345678912345678,"i":"d66c5781"},{"o":2,"a":"Email","t":0,"c":"b@test.com","v":0.12345678912,"i":"d66c5781"}]}}}"""
}
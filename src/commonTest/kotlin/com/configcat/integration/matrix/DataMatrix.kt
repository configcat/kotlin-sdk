package com.configcat.integration.matrix

interface DataMatrix {
    val sdkKeyV5: String?
    val sdkKeyV6: String?
    val data: String
    val remoteJson: String
}

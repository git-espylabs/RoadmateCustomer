package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class WalletMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("balance") val balance: String
)
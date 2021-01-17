package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class WalletTrans(
    @SerializedName("user_id") val user_id: String,
    @SerializedName("amount_credited") val walletCredit: String
)
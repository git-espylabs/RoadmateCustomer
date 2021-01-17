package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class OtpTrans(
    @SerializedName("id") val user_id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phnum") val mobile: String
)
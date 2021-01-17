package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class NotificationMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<NotificationTrans>
)
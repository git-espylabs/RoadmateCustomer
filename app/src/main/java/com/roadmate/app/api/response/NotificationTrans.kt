package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class NotificationTrans(
    @SerializedName("id") val id: String,
    @SerializedName("customertype_id") val notificationtable_typeid: String,
    @SerializedName("title") val notification_title: String,
    @SerializedName("user_id") val user_id: String,
    @SerializedName("message") val notification_message: String
)
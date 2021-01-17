package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class MoreServicesTrans(
    @SerializedName("id") val serviceId: String,
    @SerializedName("category") val serviceName: String,
    @SerializedName("image") val serviceImage: String
)
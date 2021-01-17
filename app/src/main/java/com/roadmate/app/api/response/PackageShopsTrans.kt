package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class PackageShopsTrans(
    @SerializedName("id") val shopId: String,
    @SerializedName("type") val type: String,
    @SerializedName("image") val shopImage: String,
    @SerializedName("timming") val shopTiming: String,
    @SerializedName("open_time") val open_time: String,
    @SerializedName("close_time") val close_time: String,
    @SerializedName("shopname") val shopname: String,
    @SerializedName("address") val shopAddress: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("description") val description: String,
    @SerializedName("rateaverage") val shopRating: String = "0.0",
    @SerializedName("distance") val distance: String = "0",
    var bookingStatus: Int = 0
)
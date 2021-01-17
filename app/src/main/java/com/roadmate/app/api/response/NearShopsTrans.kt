package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class NearShopsTrans(
    @SerializedName("id") val shopId: String,
    @SerializedName("shopname") val shopName: String,
    @SerializedName("image") val shopImage: String,
    @SerializedName("address") val shopAddress: String,
    @SerializedName("timming") val shopTiming: String,
    @SerializedName("open_time") val open_time: String,
    @SerializedName("close_time") val close_time: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("distance") val shopDistance: String = "0",
    @SerializedName("rateaverage") val shopRating: String = "0.0",
    @SerializedName("shop_road_assistance") val isRoadAssistanceAvailable: String = "1"
)
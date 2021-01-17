package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class ShopBookingListTrans(
//    @SerializedName("shop_id") val shop_id: String,
//    @SerializedName("shopname") val shopname: String
    @SerializedName("id") val shopId: String,
    @SerializedName("shopname") val shopName: String,
    @SerializedName("image") val shopImage: String,
    @SerializedName("address") val shopAddress: String,
    @SerializedName("timming") val shopTiming: String,
    @SerializedName("open_time") val open_time: String,
    @SerializedName("close_time") val close_time: String,
    @SerializedName("distance") val shopDistance: String = "0",
    @SerializedName("rateaverage") val shopRating: String = "0.0",
    @SerializedName("shopserviceid") val shopserviceid: String,
    @SerializedName("shop_road_assistance") val isRoadAssistanceAvailable: String = "1"
)
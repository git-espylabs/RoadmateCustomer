package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class ShopDataTrans(
    @SerializedName("id") val shopId: String,
    @SerializedName("shopname") val shopName: String,
    @SerializedName("image") val shopImage: String,
    @SerializedName("address") val shopAddress: String,
    @SerializedName("timming") val shopTiming: String,
    @SerializedName("shop_distance") val shopDistance: String = "",
    @SerializedName("shop_rating") val shopRating: String = "",
    @SerializedName("shop_road_assistance") val isRoadAssistanceAvailable: String = "",
    @SerializedName("description") val description: String,
    @SerializedName("lattitude") val shopLatitude: String,
    @SerializedName("logitude") val shopLongitude: String,
    @SerializedName("phone_number") val shopPhone: String
)
package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class RatedShopsTrans(
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("shop_name") val shopName: String,
    @SerializedName("shop_distance") val shopDiastance: String,
    @SerializedName("shop_location") val shopLocation: String,
    @SerializedName("shop_time") val shopTime: String,
    @SerializedName("shop_rating") val shopRating: String,
    @SerializedName("road_assistance") val isRoadAssistanceAvailable: String,
    @SerializedName("shop_image") val shopImage: String
)
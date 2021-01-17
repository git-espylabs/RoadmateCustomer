package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class OfferDetailsTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("shopname") val shop_name: String,
    @SerializedName("distance") val shop_distance: String,
    @SerializedName("shop_cat_id") val shop_cat_id: String,
    @SerializedName("title") val title: String,
    @SerializedName("small_desc") val small_desc: String,
    @SerializedName("normal_amount") val normal_amount: String,
    @SerializedName("offer_amount") val offer_amount: String,
    @SerializedName("vehicle_typeid") val vehicle_typeid: String,
    @SerializedName("brand_id") val brand_id: String,
    @SerializedName("model_id") val model_id: String,
    @SerializedName("offer_type") val offer_type: String,
    @SerializedName("pic") val pic: String,
    @SerializedName("offer_end_date") val offer_end_date: String,
    @SerializedName("veh_type") val veh_type: String,
    @SerializedName("brand") val brand: String,
    @SerializedName("brand_model") val brand_model: String,
    @SerializedName("offer_discount_type") val offer_discount_type: String,
    @SerializedName("discount_percentage") val discount_percentage: String
)
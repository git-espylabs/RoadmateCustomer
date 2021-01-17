package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class CustomerOfferTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("shop_cat_id") val shop_cat_id: String,
    @SerializedName("title") val title: String,
    @SerializedName("offer_amount") val offerAmount: String,
    @SerializedName("pic") val image: String,
    @SerializedName("normal_amount") val normalAMount: String,
    @SerializedName("offer_discount_type") val offer_discount_type: String,
    @SerializedName("discount_percentage") val percentage: String
)
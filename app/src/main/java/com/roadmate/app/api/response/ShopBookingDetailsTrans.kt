package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class ShopBookingDetailsTrans(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("image") val image: String,
    @SerializedName("timming") val timming: String,
    @SerializedName("open_time") val open_time: String,
    @SerializedName("close_time") val close_time: String,
    @SerializedName("shopname") val shopname: String,
    @SerializedName("address") val address: String,
    @SerializedName("status") val status: String,
    @SerializedName("phone_number") val phone_number: String,
    @SerializedName("phone_number2") val phone_number2: String,
    @SerializedName("agrimentverification_status") val agrimentverification_status: String,
    @SerializedName("pincode") val pincode: String,
    @SerializedName("description") val description: String,
    @SerializedName("lattitude") val lattitude: String,
    @SerializedName("logitude") val logitude: String,
    @SerializedName("trans_id") val trans_id: String,
    @SerializedName("pay_status") val pay_status: String,
    @SerializedName("shop_oc_status") val shop_oc_status: String,
    @SerializedName("exeid") val exeid: String
)
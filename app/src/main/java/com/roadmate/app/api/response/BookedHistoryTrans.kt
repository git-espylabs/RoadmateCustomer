package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class BookedHistoryTrans(
    @SerializedName("id") val id: String,
    @SerializedName("customer_id") val customer_id: String,
    @SerializedName("shop_id") val shop_id: String,
    @SerializedName("timeslots") val timeslots: String,
    @SerializedName("adate") val adate: String,
    @SerializedName("book_type") val book_type: String,
    @SerializedName("book_id") val book_id: String,
    @SerializedName("shop_category_id") val shop_category_id: String,
    @SerializedName("shopname") val shopname: String,
    @SerializedName("address") val address: String
)
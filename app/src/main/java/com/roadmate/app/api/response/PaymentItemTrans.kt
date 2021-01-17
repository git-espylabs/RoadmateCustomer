package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class PaymentItemTrans(
    @SerializedName("id") var itemId: String = "0",
    @SerializedName("customer_id") var customer_id: String = "0",
    @SerializedName("shopname") var itemShopName: String = "",
    @SerializedName("shop_id") var itemShopId: String = "0",
    @SerializedName("timeslots") var timeslots: String = "0",
    @SerializedName("adate") var itemShopServiceDate: String = "00/00/0000",
    @SerializedName("amount") var itemTotal: String = "0",
    var isItemSelected: Boolean = false,
    var isRedeemed: Boolean = false
){
    constructor() : this("0", "", "0","00/00/0000", "0")
}
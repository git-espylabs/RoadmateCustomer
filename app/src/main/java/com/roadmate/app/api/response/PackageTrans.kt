package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class PackageTrans(
    @SerializedName("id") val packageId: String,
    @SerializedName("image") val packageImage: String,
    @SerializedName("title") val packageName: String,
    @SerializedName("amount") val packagePrice: String,
    @SerializedName("offer_amount") val packageOfferPrice: String,
    @SerializedName("description") val packageDescription: String
)
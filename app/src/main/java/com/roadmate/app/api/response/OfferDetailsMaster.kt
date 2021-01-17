package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class OfferDetailsMaster(
    @SerializedName("error") val error: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ArrayList<OfferDetailsTrans>
)
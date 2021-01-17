package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class SearchTrans(
    @SerializedName("id") val id: String,
    @SerializedName("shopname") val shopname: String
)
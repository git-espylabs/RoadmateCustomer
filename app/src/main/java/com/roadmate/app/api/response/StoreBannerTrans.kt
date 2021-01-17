package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class StoreBannerTrans(
    @SerializedName("product_picture") val bannerImage: String
)
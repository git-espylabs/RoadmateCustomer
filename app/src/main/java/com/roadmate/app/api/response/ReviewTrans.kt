package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class ReviewTrans(
    @SerializedName("id") val reviewId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("review_count") val reviewCount: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)
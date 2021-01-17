package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class PackageFeaturesTrans(
    @SerializedName("id") val featureId: String,
    @SerializedName("feature") val feature: String
)
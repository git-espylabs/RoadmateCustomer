package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class VehicleModelTrans(
    @SerializedName("id") val vehicleModelId: String,
    @SerializedName("model") val vehicleModel: String
)
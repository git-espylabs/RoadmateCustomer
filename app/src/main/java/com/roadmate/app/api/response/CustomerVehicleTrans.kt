package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class CustomerVehicleTrans(
    @SerializedName("id") val veh_id: String,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("vehicle_type") val vehicleTypeId: String,
    @SerializedName("veh_type") val vehicleTypeName: String,
    @SerializedName("vehicle_brand") val vehicleBrandId: String,
    @SerializedName("brand") val vehicleBrandName: String,
    @SerializedName("vehicle_model") val vehicleModelId: String,
    @SerializedName("brand_model") val vehicleModelName: String="",
    @SerializedName("fuel_type") val vehicleFuelType: String



    ) {
    override fun toString(): String {
        return vehicleNumber
    }
}
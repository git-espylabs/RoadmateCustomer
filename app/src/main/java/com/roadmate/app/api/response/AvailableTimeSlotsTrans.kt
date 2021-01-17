package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class AvailableTimeSlotsTrans(
    @SerializedName("id") val id: String,
    @SerializedName("time") val name: String,
    @SerializedName("Availabel") var  isAvailable: String = "0",
    var  isSelected: Boolean = false
)
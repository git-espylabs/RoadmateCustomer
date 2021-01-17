package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class FaqTrans(
    @SerializedName("quid") val quid: String,
    @SerializedName("quname") val quname: String,
    @SerializedName("question") val question: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("anname") val anname: String
)
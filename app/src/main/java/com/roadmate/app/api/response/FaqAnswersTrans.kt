package com.roadmate.app.api.response

import com.google.gson.annotations.SerializedName

data class FaqAnswersTrans(
    @SerializedName("id") val id: String,
    @SerializedName("answer") val answer: String,
    @SerializedName("quserid") val quserid: String,
    @SerializedName("question_id") val question_id: String,
    @SerializedName("auserid") val auserid: String
)
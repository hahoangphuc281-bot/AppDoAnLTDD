package com.example.doanltdd.data.model

import com.google.gson.annotations.SerializedName

data class UserDetailResponse(
    val succeeded: Boolean,
    val message: String?,
    @SerializedName("result") val result: Customer // Quan trọng: Customer nằm trong này
)
package com.example.doanltdd.data.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("avatar") val avatar: String?,

    @SerializedName("totalOrders") val totalOrders: Int = 0,
    @SerializedName("totalSpent") val totalSpent: Double = 0.0
)
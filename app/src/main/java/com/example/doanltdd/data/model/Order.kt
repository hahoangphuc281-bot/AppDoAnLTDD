package com.example.doanltdd.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    // Backend trả về "order_id" -> map vào biến id
    @SerializedName("order_id") val id: Int,

    // Backend trả về "Username" -> map vào biến recipient
    @SerializedName("Username") val recipient: String,

    @SerializedName("order_date") val orderDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_amount") val totalAmount: Double
)
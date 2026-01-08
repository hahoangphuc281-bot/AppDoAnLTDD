package com.example.doanltdd.data.model

data class UpdateStatusRequest(
    val order_id: Int,
    val status: String
)
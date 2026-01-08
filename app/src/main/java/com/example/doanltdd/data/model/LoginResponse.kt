package com.example.doanltdd.data.model

data class LoginResponse(
    val succeeded: Boolean,
    val token: String?,
    val message: String?,
    val is_admin: Int? // Giữ lại cái này để kiểm tra Admin như code cũ
)
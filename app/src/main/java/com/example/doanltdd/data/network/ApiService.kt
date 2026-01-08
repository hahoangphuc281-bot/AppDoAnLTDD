package com.example.doanltdd.data.network

import com.example.doanltdd.data.model.LoginResponse
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.model.User
import com.example.doanltdd.data.model.OrderDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body request: User): Response<LoginResponse>

    // --- SỬA LẠI THÀNH @GET CHO ĐÚNG CHUẨN ---
    @GET("api/orders/all")
    suspend fun getOrders(): List<Order>

    // --- GIỮ NGUYÊN @GET VÀ PATH ---
    @GET("api/orders/detail/{id}")
    suspend fun getOrderDetail(@Path("id") id: Int): Response<OrderDetailResponse>
}
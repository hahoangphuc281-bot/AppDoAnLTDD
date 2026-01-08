package com.example.doanltdd.data.network

import com.example.doanltdd.data.model.LoginResponse
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.model.User
import com.example.doanltdd.data.model.OrderDetailResponse
import com.example.doanltdd.data.model.UpdateStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body request: User): Response<LoginResponse>

    @GET("api/orders/all")
    suspend fun getOrders(): List<Order>

    @GET("api/orders/detail/{id}")
    suspend fun getOrderDetail(@Path("id") id: Int): Response<OrderDetailResponse>

    @POST("api/orders/update-status")
    suspend fun updateOrderStatus(@Body request: UpdateStatusRequest): Response<Void>
}
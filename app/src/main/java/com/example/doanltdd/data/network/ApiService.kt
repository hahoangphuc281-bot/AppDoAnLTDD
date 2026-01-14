package com.example.doanltdd.data.network

import com.example.doanltdd.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/users/login")
    suspend fun login(@Body request: User): Response<LoginResponse>

    @GET("api/users/list")
    suspend fun getAllCustomers(): Response<UserListResponse>

    @GET("api/orders/all")
    suspend fun getOrders(): List<Order>

    @GET("api/orders/detail/{id}")
    suspend fun getOrderDetail(@Path("id") id: Int): Response<OrderDetailResponse>

    @POST("api/orders/update-status")
    suspend fun updateOrderStatus(@Body request: UpdateStatusRequest): Response<Void>

    // --- SỬA DÒNG NÀY ---
    @GET("api/users/{id}")
    suspend fun getCustomerDetail(@Path("id") id: Int): Response<UserDetailResponse>
    // (Đổi từ Customer thành UserDetailResponse)

    @PUT("api/users/{id}")
    suspend fun updateCustomer(
        @Path("id") id: Int,
        @Body customer: Customer
    ): Response<ResponseBody>

    @GET("api/users/{id}/orders")
    suspend fun getOrdersByUser(@Path("id") userId: Int): List<Order>
}
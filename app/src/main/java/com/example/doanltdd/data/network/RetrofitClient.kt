package com.example.doanltdd.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // SỬA LỖI: Dùng dấu hai chấm (:) trước cổng 3001, không phải dấu gạch chéo
    private const val BASE_URL = "http://10.0.2.2:3001/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
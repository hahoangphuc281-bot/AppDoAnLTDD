package com.example.doanltdd.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {


    private const val BASE_URL = "http://10.0.2.2:3001/"
//    private const val BASE_URL = "http://192.168.1.17:3001/"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val token = SessionManager.getToken()

        if (token != null) {
            // Nếu có token thì gắn vào Header
            val request = original.newBuilder()
                .header("Authorization", "Bearer $token")
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        } else {
            // Chưa có token thì cứ gửi bình thường (cho API Login)
            chain.proceed(original)
        }
    }.build()

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <--- QUAN TRỌNG: Phải thêm dòng này
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
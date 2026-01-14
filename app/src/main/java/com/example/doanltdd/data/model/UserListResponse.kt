package com.example.doanltdd.data.model
import com.google.gson.annotations.SerializedName

data class UserListResponse(
    val succeeded: Boolean,
    val message: String,
    @SerializedName("result") val result: List<Customer>
)
package com.example.doanltdd.data.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("order_id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("order_date") val orderDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("address_id") val addressId: Int?,

    // Thêm trường này để đếm số sản phẩm
    @SerializedName("total_products") val totalProducts: Int = 0,

    // Thêm trường này (dù server chưa trả về tên thì để null để tránh lỗi crash)
    @SerializedName("recipient_name") val recipient: String? = null
)
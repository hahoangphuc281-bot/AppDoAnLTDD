package com.example.doanltdd.data.model
import com.google.gson.annotations.SerializedName

// Model tổng trả về từ API detail
data class OrderDetailResponse(
    @SerializedName("order_id") val id: Int,
    @SerializedName("Username") val recipient: String,

    // --- THÊM DÒNG NÀY ---
    @SerializedName("phone") val phone: String?,

    @SerializedName("order_date") val orderDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("shipping_address") val address: String?,
    @SerializedName("products") val products: List<ProductInOrder>
)

// Model sản phẩm con bên trong
data class ProductInOrder(
    @SerializedName("Name") val name: String,
    @SerializedName("Image") val image: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price_at_purchase") val price: Double
)
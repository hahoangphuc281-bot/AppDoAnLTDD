package com.example.doanltdd.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch // Đảm bảo import này để dùng viewModelScope.launch

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        fetchOrdersFromApi()
    }

    private fun fetchOrdersFromApi() {
        viewModelScope.launch {
            try {
                // 1. Gọi API qua instance của Retrofit
                val response = RetrofitClient.instance.getOrders()

                // 2. .map này sẽ tự động nhận diện là của Kotlin List sau khi xóa import sai
                val processedList = response.map { order ->
                    // .copy() lúc này sẽ nhận diện được cả status và recipient
                    order.copy(status = mapStatus(order.status))
                }
                    .sortedByDescending { it.orderDate }
                    .take(5)

                _orders.value = processedList
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Lỗi: ${e.message}")
            }
        }
    }

    private fun mapStatus(sqlStatus: String): String {
        return when (sqlStatus) {
            "Pending" -> "Chờ xác nhận"
            "Confirmed" -> "Đã xác nhận"
            "Shipping" -> "Đang giao"
            "Completed" -> "Đã Giao"
            "Cancelled" -> "Đã hủy"
            else -> sqlStatus
        }
    }
}

package com.example.doanltdd.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    init {
        fetchOrdersFromApi()
    }

    fun fetchOrdersFromApi() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getOrders()
                val processedList = response.map { order ->
                    order.copy(status = mapStatus(order.status))
                }.sortedByDescending { it.orderDate }
                // --- ĐÃ XÓA DÒNG .take(5) ĐỂ LẤY HẾT ---

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

package com.example.doanltdd.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<Order>>(emptyList())

    // --- KHẮC PHỤC LỖI 1: THÊM DÒNG NÀY ĐỂ HOMESCREEN GỌI ĐƯỢC ---
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _filterStatus = MutableStateFlow("All")

    // Biến này dùng cho màn hình Quản lý đơn hàng (để lọc)
    val filteredOrders: StateFlow<List<Order>> = combine(_orders, _filterStatus) { orders, filter ->
        if (filter == "All") {
            orders
        } else {
            orders.filter { it.status == filter }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchOrdersFromApi()
    }

    fun fetchOrdersFromApi() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getOrders()
                val processedList = response.map { order ->
                    // Giữ nguyên status tiếng Anh từ Server để xử lý logic màu sắc bên UI
                    order.copy(status = order.status)
                }.sortedByDescending { it.orderDate }

                _orders.value = processedList
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Lỗi: ${e.message}")
            }
        }
    }

    fun setFilter(status: String) {
        _filterStatus.value = status
    }
}
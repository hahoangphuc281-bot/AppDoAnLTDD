package com.example.doanltdd.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.Customer
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel : ViewModel() {
    // Danh sách khách hàng (cho màn hình Quản lý)
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers: StateFlow<List<Customer>> = _customers.asStateFlow()

    private val _customerOrders = MutableStateFlow<List<Order>>(emptyList())
    val customerOrders: StateFlow<List<Order>> = _customerOrders.asStateFlow()


    // 1. Khách hàng đang được chọn để sửa
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer: StateFlow<Customer?> = _selectedCustomer.asStateFlow()

    // 2. Trạng thái cập nhật (Thông báo thành công/thất bại)
    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> = _updateStatus.asStateFlow()

    // 3. Trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchCustomers()
    }

    // Hàm lấy danh sách tất cả (cũ)
    fun fetchCustomers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Lưu ý: Đảm bảo ApiService đã có hàm getAllCustomers trả về Response<UserListResponse> như hướng dẫn trước
                val response = RetrofitClient.instance.getAllCustomers()
                if (response.isSuccessful && response.body() != null) {
                    _customers.value = response.body()!!.result
                }
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Lỗi lấy danh sách: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- CÁC HÀM MỚI BỊ THIẾU ---

    // Hàm lấy chi tiết 1 khách hàng theo ID
    fun getCustomerById(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _selectedCustomer.value = null // Reset trước khi load
            try {
                val response = RetrofitClient.instance.getCustomerDetail(id)
                if (response.isSuccessful && response.body() != null) {
                    _selectedCustomer.value = response.body()!!.result
                } else {
                    _selectedCustomer.value = _customers.value.find { it.id == id }
                }
            } catch (e: Exception) {
                _selectedCustomer.value = _customers.value.find { it.id == id }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm gửi yêu cầu cập nhật lên Server
    fun updateCustomer(id: Int, username: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Tạo object customer tạm để gửi đi (avatar giữ nguyên nếu có)
                val currentAvatar = _selectedCustomer.value?.avatar
                val updatedInfo = Customer(id, username, email, phone, currentAvatar)

                val response = RetrofitClient.instance.updateCustomer(id, updatedInfo)

                if (response.isSuccessful) {
                    _updateStatus.value = "Cập nhật thành công!"
                    fetchCustomers() // Load lại danh sách mới nhất
                } else {
                    _updateStatus.value = "Lỗi: ${response.code()} - ${response.message()}"
                }
            } catch (e: Exception) {
                _updateStatus.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Hàm xóa trạng thái thông báo
    fun clearStatus() {
        _updateStatus.value = null
    }

    fun fetchCustomerOrders(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val orders = RetrofitClient.instance.getOrdersByUser(userId)
                _customerOrders.value = orders
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Lỗi lấy đơn hàng: ${e.message}")
                _customerOrders.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
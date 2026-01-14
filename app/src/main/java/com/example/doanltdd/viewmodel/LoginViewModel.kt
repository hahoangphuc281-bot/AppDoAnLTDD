package com.example.doanltdd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.User
import com.example.doanltdd.data.network.RetrofitClient
import com.example.doanltdd.data.network.SessionManager // Import cái này
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // ... (Giữ nguyên các biến State cũ)
    private val _loginState = MutableStateFlow<Boolean?>(null)
    val loginState: StateFlow<Boolean?> = _loginState
    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = User(username, pass)
                val response = RetrofitClient.instance.login(request)

                if (response.isSuccessful && response.body()?.succeeded == true) {
                    val body = response.body()!!
                    val isAdmin = body.is_admin ?: 0

                    if (isAdmin == 1) {
                        // --- LƯU TOKEN TẠI ĐÂY ---
                        body.token?.let { token ->
                            SessionManager.saveToken(token)
                        }
                        // -------------------------
                        _loginState.value = true
                    } else {
                        _loginState.value = false
                        _errorMessage.value = "Bạn không có quyền Admin!"
                    }
                } else {
                    _loginState.value = false
                    _errorMessage.value = "Sai tài khoản hoặc mật khẩu"
                }
            } catch (e: Exception) {
                _loginState.value = false
                _errorMessage.value = "Lỗi kết nối: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _loginState.value = null
        _errorMessage.value = ""
    }
}
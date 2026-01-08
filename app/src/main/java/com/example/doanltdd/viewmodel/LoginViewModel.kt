package com.example.doanltdd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.doanltdd.data.model.User
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
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
                    val isAdmin = response.body()?.is_admin ?: 0
                    if (isAdmin == 1) {
                        _loginState.value = true
                    } else {
                        _loginState.value = false
                        _errorMessage.value = "Bạn không có quyền Admin!"
                    }
                } else {
                    _loginState.value = false
                    val errorBody = response.errorBody()?.string()
                    _errorMessage.value = errorBody ?: "Lỗi đăng nhập (Sai pass/user)"
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
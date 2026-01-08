package com.example.doanltdd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doanltdd.screen.HomeScreen
import com.example.doanltdd.screen.LoginScreen
// Đảm bảo bạn đã tạo 2 file màn hình này trong package screen, nếu chưa thì import sẽ báo đỏ
import com.example.doanltdd.screen.OrderManagementScreen
import com.example.doanltdd.screen.OrderDetailScreen

// 1. Cập nhật thêm route chi tiết vào AppRoutes
object AppRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ORDER_MANAGEMENT = "order_management"
    const val ORDER_DETAIL = "order_detail" // Tên gốc
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        // --- MÀN HÌNH 1: ĐĂNG NHẬP ---
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // --- MÀN HÌNH 2: TRANG CHỦ ---
        composable(AppRoutes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToOrders = {
                    // CẬP NHẬT Ở ĐÂY: Không log nữa mà chuyển trang thật
                    navController.navigate(AppRoutes.ORDER_MANAGEMENT)
                }
            )
        }

        // --- MÀN HÌNH 3: QUẢN LÝ ĐƠN HÀNG (LIST) ---
        // Đây là phần bạn thêm vào từ "PHẦN 4"
        composable(AppRoutes.ORDER_MANAGEMENT) {
            OrderManagementScreen(
                onBack = {
                    navController.popBackStack() // Quay về Home
                },
                onNavigateToDetail = { orderId ->
                    // Chuyển sang màn chi tiết, nối thêm ID vào đuôi URL
                    navController.navigate("${AppRoutes.ORDER_DETAIL}/$orderId")
                }
            )
        }

        // --- MÀN HÌNH 4: CHI TIẾT ĐƠN HÀNG ---
        // Đây là phần bạn thêm vào từ "PHẦN 4"
        composable("${AppRoutes.ORDER_DETAIL}/{orderId}") { backStackEntry ->
            // Lấy orderId từ đường dẫn
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "0"

            OrderDetailScreen(
                orderId = orderId,
                onBack = {
                    navController.popBackStack() // Quay về danh sách đơn hàng
                }
            )
        }
    }
}
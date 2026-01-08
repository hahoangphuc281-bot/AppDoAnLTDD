package com.example.doanltdd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doanltdd.screen.HomeScreen
import com.example.doanltdd.screen.LoginScreen
import com.example.doanltdd.screen.OrderManagementScreen
import com.example.doanltdd.screen.OrderDetailScreen

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
                onLogout = { /* logic logout */ },
                onNavigateToOrders = { navController.navigate(AppRoutes.ORDER_MANAGEMENT) },
                // Thêm dòng này:
                onNavigateToDetail = { orderId ->
                    navController.navigate("${AppRoutes.ORDER_DETAIL}/$orderId")
                }
            )
        }

        // --- MÀN HÌNH 3: QUẢN LÝ ĐƠN HÀNG (LIST) ---
        composable(AppRoutes.ORDER_MANAGEMENT) {
            OrderManagementScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToDetail = { orderId ->
                    navController.navigate("${AppRoutes.ORDER_DETAIL}/$orderId")
                }
            )
        }

        // --- MÀN HÌNH 4: CHI TIẾT ĐƠN HÀNG ---
        composable("${AppRoutes.ORDER_DETAIL}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "0"

            OrderDetailScreen(
                orderId = orderId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
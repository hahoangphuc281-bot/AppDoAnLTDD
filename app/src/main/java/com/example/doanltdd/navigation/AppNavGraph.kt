package com.example.doanltdd.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doanltdd.screen.HomeScreen
import com.example.doanltdd.screen.LoginScreen
import com.example.doanltdd.screen.OrderManagementScreen
import com.example.doanltdd.screen.OrderDetailScreen
import com.example.doanltdd.screen.CustomerManagementScreen
import com.example.doanltdd.screen.EditCustomerScreen
import com.example.doanltdd.screen.CustomerOrdersScreen

object AppRoutes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val ORDER_MANAGEMENT = "order_management"
    const val ORDER_DETAIL = "order_detail"
    const val CUSTOMER_MANAGEMENT = "customer_management"
    const val EDIT_CUSTOMER = "edit_customer"

    const val CUSTOMER_ORDERS = "customer_orders"
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.LOGIN) {

        // 1. Màn hình Đăng nhập
        composable(AppRoutes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 2. Màn hình Trang chủ (Home)
        composable(AppRoutes.HOME) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppRoutes.LOGIN) { popUpTo(0) }
                },
                onNavigateToOrders = { navController.navigate(AppRoutes.ORDER_MANAGEMENT) },
                onNavigateToDetail = { orderId -> navController.navigate("${AppRoutes.ORDER_DETAIL}/$orderId") },
                onNavigateToCustomers = { navController.navigate(AppRoutes.CUSTOMER_MANAGEMENT) }
            )
        }

        // 3. Màn hình Quản lý đơn hàng
        composable(AppRoutes.ORDER_MANAGEMENT) {
            OrderManagementScreen(
                onBack = { navController.popBackStack() },
                onNavigateToDetail = { orderId -> navController.navigate("${AppRoutes.ORDER_DETAIL}/$orderId") }
            )
        }

        // 4. Màn hình Chi tiết đơn hàng
        composable("${AppRoutes.ORDER_DETAIL}/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: "0"
            OrderDetailScreen(orderId = orderId, onBack = { navController.popBackStack() })
        }

        // 5. Màn hình Quản lý Khách hàng
        composable(AppRoutes.CUSTOMER_MANAGEMENT) {
            CustomerManagementScreen(navController = navController)
        }

        // 6. Màn hình Sửa Khách hàng (Edit)
        composable("${AppRoutes.EDIT_CUSTOMER}/{customerId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("customerId") ?: "0"
            EditCustomerScreen(navController, id)
        }

        composable("${AppRoutes.CUSTOMER_ORDERS}/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: "0"
            CustomerOrdersScreen(navController, userId)
        }
    }
}
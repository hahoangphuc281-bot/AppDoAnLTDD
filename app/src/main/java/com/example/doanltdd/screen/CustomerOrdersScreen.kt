package com.example.doanltdd.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.navigation.AppRoutes
import com.example.doanltdd.viewmodel.CustomerViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrdersScreen(
    navController: NavController,
    userId: String,
    viewModel: CustomerViewModel = viewModel()
) {
    val orders by viewModel.customerOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Gọi API lấy đơn khi vào màn hình
    LaunchedEffect(userId) {
        viewModel.fetchCustomerOrders(userId.toIntOrNull() ?: 0)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lịch sử mua hàng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                Text(
                    "Khách hàng này chưa mua đơn nào",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orders) { order ->
                        CustomerOrderItem(order) {
                            // Chuyển hướng sang chi tiết đơn hàng
                            navController.navigate("${AppRoutes.ORDER_DETAIL}/${order.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerOrderItem(order: Order, onClick: () -> Unit) {
    val formatter = DecimalFormat("#,###")

    // Format ngày tháng cho đẹp (nếu chuỗi ngày trả về dạng ISO)
    val displayDate = try {
        // Giả sử server trả về dạng: 2026-01-09T19:09:27.000Z
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = parser.parse(order.orderDate ?: "")
        val showFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        showFormat.format(date!!)
    } catch (e: Exception) {
        order.orderDate ?: "Không xác định"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Dòng 1: Mã đơn + Trạng thái
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Đơn #${order.id}", // SỬA: Dùng order.id thay vì order_id
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.status ?: "Pending",
                    color = if (order.status == "Completed") Color(0xFF2E7D32) else Color(0xFFF57C00),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dòng 2: Ngày đặt
            Text(text = "Ngày đặt: $displayDate", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(8.dp))

            // Dòng 3: Tổng tiền + Số lượng SP
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${order.totalProducts} sản phẩm",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = "${formatter.format(order.totalAmount)}đ",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    fontSize = 16.sp
                )
            }
        }
    }
}
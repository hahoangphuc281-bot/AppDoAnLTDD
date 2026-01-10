package com.example.doanltdd.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.viewmodel.OrderViewModel
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
// Import icon cái phễu (FilterList)
import androidx.compose.material.icons.filled.FilterList

@Composable
fun OrderManagementScreen(
    viewModel: OrderViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit
) {
    // QUAN TRỌNG: Lắng nghe filteredOrders thay vì orders gốc
    val orders by viewModel.filteredOrders.collectAsState()

    // Biến trạng thái để đóng mở menu lọc
    var showFilterMenu by remember { mutableStateOf(false) }

    // Logic tự load lại khi quay về (Giữ nguyên code cũ của bạn)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchOrdersFromApi()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FB))) {

        // --- HEADER CÓ NÚT LỌC ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Căn đều 2 bên
        ) {
            // Bên trái: Nút Back + Tiêu đề
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
                Text("Quản lý đơn hàng", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
            }


            Box {
                // Nút cái phễu
                IconButton(
                    onClick = { showFilterMenu = true }, // Bấm vào thì mở menu
                    modifier = Modifier.background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Lọc", tint = Color.Black)
                }

                DropdownMenu(
                    expanded = showFilterMenu,
                    onDismissRequest = { showFilterMenu = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    val filters = listOf(
                        "All" to "Tất cả",
                        "Pending" to "Chờ xác nhận",
                        "Confirmed" to "Đã xác nhận",
                        "Shipping" to "Đang giao",
                        "Completed" to "Đã Giao",
                        "Cancelled" to "Đã hủy"
                    )

                    filters.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.setFilter(key) // Gọi hàm trong ViewModel
                                showFilterMenu = false   // Đóng menu
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (orders.isEmpty()) {
                item {
                    Text(
                        "Không có đơn hàng nào",
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                items(orders) { order ->
                    OrderManagementItem(
                        order = order,
                        onDetailClick = {
                            try { onNavigateToDetail(order.id.toInt()) } catch (e: Exception) { }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderManagementItem(order: Order, onDetailClick: () -> Unit) {
    // ... (Code cũ của bạn giữ nguyên)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Mã đơn hàng : #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Người nhận: ${order.recipient ?: "Khách lẻ"}", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusLabel(order.status)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDetailClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("Chi Tiết", color = Color.Black, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun StatusLabel(status: String) {
    val (displayStatus, bgColor, txtColor) = when (status) {
        "Pending" -> Triple("Chờ xác nhận", Color(0xFFE3F2FD), Color(0xFF2196F3))
        "Confirmed" -> Triple("Đã xác nhận", Color(0xFFE8EAF6), Color(0xFF3F51B5))
        "Shipping" -> Triple("Đang giao", Color(0xFFFFF9C4), Color(0xFFFBC02D))
        "Completed" -> Triple("Đã Giao", Color(0xFFE8F5E9), Color(0xFF4CAF50))
        "Cancelled" -> Triple("Đã hủy", Color(0xFFFFEBEE), Color(0xFFF44336))
        else -> Triple(status, Color.LightGray, Color.Black)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(50)) {
        Text(
            text = displayStatus,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            fontSize = 10.sp,
            color = txtColor,
            fontWeight = FontWeight.Bold
        )
    }
}
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.viewmodel.OrderViewModel

@Composable
fun OrderManagementScreen(
    viewModel: OrderViewModel = viewModel(),
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit
) {
    val orders by viewModel.orders.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FB))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text("Quản lý đơn hàng", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(orders) { order ->
                OrderManagementItem(order, onDetailClick = { onNavigateToDetail(order.id) })
            }
        }
    }
}

@Composable
fun OrderManagementItem(order: Order, onDetailClick: () -> Unit) {
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
                Text("Người nhận: ${order.recipient}", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusLabel(order.status) // Dùng lại hàm StatusLabel cũ
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
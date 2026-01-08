package com.example.doanltdd.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd.data.model.OrderDetailResponse
import com.example.doanltdd.data.model.ProductInOrder
import com.example.doanltdd.data.network.RetrofitClient

@Composable
fun OrderDetailScreen(orderId: String, onBack: () -> Unit) {
    var orderDetail by remember { mutableStateOf<OrderDetailResponse?>(null) }

    // Gọi API lấy chi tiết khi màn hình mở
    LaunchedEffect(orderId) {
        try {
            val response = RetrofitClient.instance.getOrderDetail(orderId.toInt())
            if (response.isSuccessful) {
                orderDetail = response.body()
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    if (orderDetail == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        val order = orderDetail!!
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // Header: Đơn hàng #ID
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                Text("Đơn hàng : #${order.id}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Hàng Trạng thái + Nút Thay đổi (Màu xanh)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Trạng thái:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        StatusLabel(order.status) // Hàm cũ
                    }
                    Button(
                        onClick = { /* Logic đổi trạng thái */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)), // Màu xanh nhạt
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Thay đổi trạng thái ▶", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Thông tin người nhận, ngày, địa chỉ
                InfoRow("Người nhận", order.recipient)
                InfoRow("Ngày lên đơn", order.orderDate.take(10)) // Cắt lấy ngày
                InfoRow("Địa chỉ", order.address ?: "Chưa cập nhật")

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }

            // Danh sách sản phẩm (Product List)
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(order.products) { product ->
                    ProductItemRow(product)
                }
            }

            // Footer: Tổng tiền
            Column(modifier = Modifier.padding(16.dp)) {
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng cộng :", fontWeight = FontWeight.Bold)
                    Text("${order.totalAmount} đ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Nút Trở lại (Góc phải dưới)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Trở lại >", color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, modifier = Modifier.width(100.dp), fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ProductItemRow(product: ProductInOrder) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            // Ảnh sản phẩm (Cần thư viện Coil)
            AsyncImage(
                model = product.image,
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                Text("x${product.quantity}", fontSize = 12.sp, color = Color.Gray)
            }
            Text("${product.price} đ", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
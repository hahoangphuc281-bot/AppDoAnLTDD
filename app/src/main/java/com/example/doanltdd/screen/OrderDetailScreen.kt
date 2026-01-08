package com.example.doanltdd.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.doanltdd.data.model.OrderDetailResponse
import com.example.doanltdd.data.model.ProductInOrder
import com.example.doanltdd.data.model.UpdateStatusRequest
import com.example.doanltdd.data.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun OrderDetailScreen(orderId: String, onBack: () -> Unit) {
    var orderDetail by remember { mutableStateOf<OrderDetailResponse?>(null) }
    var showDialog by remember { mutableStateOf(false) } // Trạng thái hiển thị Dialog chọn Status
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Hàm load lại dữ liệu
    fun loadData() {
        scope.launch {
            try {
                val response = RetrofitClient.instance.getOrderDetail(orderId.toInt())
                if (response.isSuccessful) {
                    orderDetail = response.body()
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // Gọi API lần đầu
    LaunchedEffect(orderId) { loadData() }

    // Hàm gọi API cập nhật status
    fun updateStatus(newStatusEnglish: String) {
        scope.launch {
            try {
                val request = UpdateStatusRequest(orderId.toInt(), newStatusEnglish)
                val response = RetrofitClient.instance.updateOrderStatus(request)
                if (response.isSuccessful) {
                    Toast.makeText(context, "Đã cập nhật!", Toast.LENGTH_SHORT).show()
                    loadData() // Load lại trang để thấy status mới
                    showDialog = false // Đóng dialog
                } else {
                    Toast.makeText(context, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (orderDetail == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        val order = orderDetail!!
        Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
            // ... (Header giữ nguyên) ...
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                Text("Đơn hàng : #${order.id}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Hàng Trạng thái + Nút Thay đổi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Trạng thái:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        // Map status từ Anh sang Việt để hiển thị
                        StatusLabel(mapStatusToVietnamese(order.status))
                    }
                    Button(
                        onClick = { showDialog = true }, // Mở Dialog
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE8F5E9)),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text("Thay đổi trạng thái ▶", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // ... (Phần InfoRow, Product List giữ nguyên như code cũ của bạn) ...
                Spacer(modifier = Modifier.height(12.dp))
                InfoRow("Người nhận", order.recipient)
                InfoRow("Ngày lên đơn", order.orderDate.take(10))
                InfoRow("Địa chỉ", order.address ?: "Chưa cập nhật")

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray, thickness = 0.5.dp)
            }

            // Danh sách sản phẩm
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(order.products) { product ->
                    ProductItemRow(product)
                }
            }

            // ... (Footer giữ nguyên) ...
            Column(modifier = Modifier.padding(16.dp)) {
                Divider()
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tổng cộng :", fontWeight = FontWeight.Bold)
                    Text("${order.totalAmount} đ", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(20.dp))

                // Nút Trở lại (đặt trong Box để căn phải)
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

        // --- HỘP THOẠI CHỌN TRẠNG THÁI (DIALOG) ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Cập nhật trạng thái") },
                text = {
                    Column {
                        // Danh sách các trạng thái có thể chọn
                        // Key: Tiếng Anh (Gửi lên Server), Value: Tiếng Việt (Hiển thị)
                        val statusOptions = listOf(
                            "Pending" to "Chờ xác nhận",
                            "Confirmed" to "Đã xác nhận",
                            "Shipping" to "Đang giao",
                            "Completed" to "Đã Giao",
                            "Cancelled" to "Đã hủy"
                        )

                        statusOptions.forEach { (eng, viet) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { updateStatus(eng) } // Chọn cái nào thì update cái đó
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (order.status == eng), // Nếu trùng status hiện tại thì tick
                                    onClick = { updateStatus(eng) }
                                )
                                Text(text = viet, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Đóng") }
                }
            )
        }
    }
}

// Hàm hỗ trợ map status hiển thị
fun mapStatusToVietnamese(status: String): String {
    return when (status) {
        "Pending" -> "Chờ xác nhận"
        "Confirmed" -> "Đã xác nhận"
        "Shipping" -> "Đang giao"
        "Completed" -> "Đã Giao"
        "Cancelled" -> "Đã hủy"
        else -> status
    }
}

// ... Giữ nguyên ProductItemRow và InfoRow của bạn
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
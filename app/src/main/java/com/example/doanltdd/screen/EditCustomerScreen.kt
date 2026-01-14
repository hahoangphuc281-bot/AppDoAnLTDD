package com.example.doanltdd.screen

import android.widget.Toast
import androidx.compose.foundation.clickable // 1. Thêm import này
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.doanltdd.navigation.AppRoutes // 2. Thêm import này
import com.example.doanltdd.viewmodel.CustomerViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCustomerScreen(
    navController: NavController,
    customerId: String,
    viewModel: CustomerViewModel = viewModel()
) {
    val context = LocalContext.current
    val customer by viewModel.selectedCustomer.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load dữ liệu khi vào màn hình
    LaunchedEffect(customerId) {
        viewModel.getCustomerById(customerId.toIntOrNull() ?: 0)
    }

    // State lưu dữ liệu nhập
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Khi customer load xong -> điền vào ô nhập
    LaunchedEffect(customer) {
        customer?.let {
            username = it.username
            email = it.email ?: ""
            phone = it.phone ?: ""
        }
    }

    // Xử lý thông báo
    LaunchedEffect(updateStatus) {
        updateStatus?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.contains("thành công")) {
                navController.popBackStack()
            }
            viewModel.clearStatus()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sửa thông tin", fontWeight = FontWeight.Bold) },
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
            if (isLoading && customer == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (customer != null) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // --- PHẦN THỐNG KÊ ---
                    val formatter = DecimalFormat("#,###")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Ô Tổng đơn (ĐÃ THÊM SỰ KIỆN CLICK)
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), // Xanh dương nhạt
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    // Chuyển sang màn hình xem lịch sử mua hàng của khách này
                                    navController.navigate("${AppRoutes.CUSTOMER_ORDERS}/${customer!!.id}")
                                }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Đã mua", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "${customer!!.totalOrders} đơn",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                            }
                        }

                        // Ô Tổng tiền
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Xanh lá nhạt
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Tổng chi tiêu", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "${formatter.format(customer!!.totalSpent)}đ",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                    // ---------------------------------------------

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Tên người dùng") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Số điện thoại") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.updateCustomer(
                                customer!!.id,
                                username,
                                email,
                                phone
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("Lưu thay đổi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            } else {
                Text("Không tìm thấy thông tin", modifier = Modifier.align(Alignment.Center))
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
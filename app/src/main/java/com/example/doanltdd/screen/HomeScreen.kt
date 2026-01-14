package com.example.doanltdd.screen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd.R
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.viewmodel.OrderViewModel
import java.text.DecimalFormat

@Composable
fun HomeScreen(
    viewModel: OrderViewModel = viewModel(),
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onNavigateToCustomers: () -> Unit // --- 1. THÊM CALLBACK NÀY ---
) {
    val orders by viewModel.orders.collectAsState()
    val recentOrders = orders.take(5)

    // --- TÍNH TOÁN THỐNG KÊ ---
    val totalOrdersCount = orders.size

    // Đổi 'totalPrice' thành 'totalAmount' cho khớp với Model Order
    val totalRevenue = orders.filter { it.status != "Cancelled" }.sumOf { it.totalAmount }

    var isMenuOpen by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchOrdersFromApi()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
        ) {
            HomeHeader()

            // --- CHÈN PHẦN THỐNG KÊ VÀO ĐÂY ---
            DashboardStatistics(
                totalOrders = totalOrdersCount,
                totalRevenue = totalRevenue
            )
            // ----------------------------------

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Các đơn hàng gần đây",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D2939)
                )

                IconButton(
                    onClick = { isMenuOpen = true },
                    modifier = Modifier
                        .size(40.dp)
                        .shadow(2.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clip(CircleShape)
                ) {
                    Icon(
                        Icons.Default.GridView,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recentOrders) { order ->
                    OrderCardItem(
                        order = order,
                        onClick = {
                            try {
                                onNavigateToDetail(order.id)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    )
                }

                // Thêm khoảng trống dưới cùng để không bị che bởi menu
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }

        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuOpen = false }
            )
        }

        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it }),
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            SideMenuContent(
                onClose = { isMenuOpen = false },
                onLogout = {
                    isMenuOpen = false
                    onLogout()
                },
                onNavigateToOrders = {
                    isMenuOpen = false
                    onNavigateToOrders()
                },
                onNavigateToCustomers = { // --- 2. TRUYỀN CALLBACK ---
                    isMenuOpen = false
                    onNavigateToCustomers()
                }
            )
        }
    }
}

// --- COMPOSABLE HIỂN THỊ 2 Ô THỐNG KÊ ---
@Composable
fun DashboardStatistics(totalOrders: Int, totalRevenue: Double) {
    val formatter = DecimalFormat("#,###")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Ô Tổng đơn hàng
        StatCard(
            title = "Tổng đơn hàng",
            value = totalOrders.toString(),
            icon = Icons.Default.ReceiptLong,
            colorStart = Color(0xFF42A5F5), // Xanh dương nhạt
            colorEnd = Color(0xFF1976D2),   // Xanh dương đậm
            modifier = Modifier.weight(1f)
        )

        // Ô Tổng doanh thu
        StatCard(
            title = "Doanh thu",
            value = "${formatter.format(totalRevenue)}đ",
            icon = Icons.Default.AttachMoney,
            colorStart = Color(0xFF66BB6A), // Xanh lá nhạt
            colorEnd = Color(0xFF388E3C),   // Xanh lá đậm
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colorStart: Color,
    colorEnd: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(colorStart, colorEnd)
                    )
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
// ------------------------------------------------

@Composable
fun OrderCardItem(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Mã đơn: #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)

                // --- SỬA DÒNG NÀY ĐỂ HẾT LỖI ---
                // Thay vì hiển thị tên người nhận (chưa có), hiển thị ID khách hàng
                Text("Khách hàng: ID ${order.userId}", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(4.dp))

                // Gọi hàm hiển thị Status
                HOMEStatusLabel(order.status ?: "Pending")
            }

            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SideMenuContent(
    onClose: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToCustomers: () -> Unit // --- 3. THÊM THAM SỐ VÀO ĐÂY ---
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(Color(0xFFEFEFEF))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        MenuItem(
            text = "Home",
            icon = Icons.Default.Home,
            isActive = true,
            onClick = onClose
        )

        Spacer(modifier = Modifier.height(12.dp))

        MenuItem(
            text = "Quản lý đơn hàng",
            icon = Icons.Default.ListAlt,
            isActive = false,
            onClick = onNavigateToOrders
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- 4. THÊM MỤC MENU MỚI ---
        MenuItem(
            text = "Quản lý khách hàng",
            icon = Icons.Default.Person, // Icon người dùng
            isActive = false,
            onClick = onNavigateToCustomers
        )
        // ----------------------------

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Đăng xuất", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MenuItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isActive: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isActive) Color.Gray.copy(alpha = 0.4f) else Color.White
    val contentColor = if (isActive) Color.Black.copy(alpha = 0.6f) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(if (isActive) Color.Gray else Color.LightGray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = contentColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.logo_nuochoa),
                contentDescription = "Logo App",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                " N Perfume",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1A237E)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            BadgedBox(badge = { Badge { Text("!") } }) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.Blue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AccountCircle, null, Modifier.size(32.dp), tint = Color.Gray)
                Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HOMEStatusLabel(status: String) {
    val (displayStatus, bgColor, txtColor) = when (status) {
        "Pending"   -> Triple("Chờ xác nhận", Color(0xFFE3F2FD), Color(0xFF2196F3))
        "Confirmed" -> Triple("Đã xác nhận", Color(0xFFE8EAF6), Color(0xFF3F51B5))
        "Shipping"  -> Triple("Đang giao", Color(0xFFFFF9C4), Color(0xFFFBC02D))
        "Completed" -> Triple("Đã Giao", Color(0xFFE8F5E9), Color(0xFF4CAF50))
        "Cancelled" -> Triple("Đã hủy", Color(0xFFFFEBEE), Color(0xFFF44336))
        else        -> Triple(status, Color(0xFFFFEBEE), Color(0xFFF44336))
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
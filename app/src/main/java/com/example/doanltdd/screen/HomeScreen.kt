package com.example.doanltdd.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanltdd.R
import com.example.doanltdd.data.model.Order
import com.example.doanltdd.viewmodel.OrderViewModel
import androidx.compose.foundation.shape.CircleShape


@Composable
fun HomeScreen(
    viewModel: OrderViewModel = viewModel(),
    onLogout: () -> Unit, // Callback để đăng xuất
    onNavigateToOrders: () -> Unit // Callback để qua màn quản lý đơn
) {
    val orders by viewModel.orders.collectAsState()

    // Biến trạng thái để đóng mở Menu
    var isMenuOpen by remember { mutableStateOf(false) }

    // Dùng Box để xếp chồng các lớp lên nhau (Nội dung chính nằm dưới, Menu nằm trên)
    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. NỘI DUNG CHÍNH (MAIN CONTENT) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
        ) {
            HomeHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Hàng tiêu đề + Nút 4 ô vuông
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

                // NÚT 4 Ô VUÔNG (GRID VIEW) -> Bấm vào để mở Menu
                IconButton(
                    onClick = { isMenuOpen = true },
                    modifier = Modifier
                        .size(40.dp) // Tăng kích thước lên chút cho đẹp khi làm hình tròn
                        .shadow(2.dp, CircleShape) // Đổ bóng theo hình tròn
                        .background(Color.White, CircleShape) // Nền trắng hình tròn
                        .clip(CircleShape) // Cắt toàn bộ nội dung thành hình tròn
                ) {
                    Icon(
                        Icons.Default.GridView,
                        contentDescription = null,
                        tint = Color.Gray, // Màu icon xám
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    OrderCardItem(order)
                }
            }
        }

        // --- 2. LỚP PHỦ TỐI (SCRIM) ---
        // Khi menu mở, hiện lớp đen mờ, bấm vào thì đóng menu
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isMenuOpen = false } // Bấm ra ngoài thì đóng menu
            )
        }

        // --- 3. MENU TRƯỢT TỪ PHẢI SANG ---
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = slideInHorizontally(initialOffsetX = { it }), // Trượt từ phải vào
            exit = slideOutHorizontally(targetOffsetX = { it }),  // Trượt ra phải
            modifier = Modifier.align(Alignment.CenterEnd) // Căn lề phải
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
                }
            )
        }
    }
}

// --- GIAO DIỆN CỦA MENU BÊN PHẢI (ĐÃ CẬP NHẬT) ---
@Composable
fun SideMenuContent(
    onClose: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToOrders: () -> Unit
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

        // Item 1: Home (Bo tròn mạnh hơn)
        MenuItem(
            text = "Home",
            icon = Icons.Default.Edit,
            isActive = true,
            onClick = onClose
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Item 2: Quản lý đơn hàng
        MenuItem(
            text = "Quản lý đơn hàng",
            icon = Icons.Default.Image,
            isActive = false,
            onClick = onNavigateToOrders
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- ĐÃ XÓA NÚT BACK (MŨI TÊN) TẠI ĐÂY ---

        // Nút Đăng xuất
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(50), // Bo tròn nút đăng xuất luôn
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Đăng xuất", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

// --- ITEM MENU ĐÃ ĐƯỢC BO TRÒN (TRÒN LẠI) ---
@Composable
fun MenuItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isActive: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isActive) Color.Gray.copy(alpha = 0.4f) else Color.White
    val contentColor = if (isActive) Color.Black.copy(alpha = 0.6f) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(50)) // <--- SỬA TẠI ĐÂY: Bo tròn hoàn toàn (Pill shape)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(
                    if(isActive) Color.Gray else Color.LightGray,
                    CircleShape // Icon bên trong cũng bo tròn theo
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = contentColor, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}
// --- CÁC COMPONENT CON (ĐỂ Ở CUỐI FILE) ---

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo và Tên App bên trái
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Bạn có thể thay icon này bằng icon logo của bạn
            Icon(Icons.Default.HomeRepairService, null, modifier = Modifier.size(28.dp), tint = Color(0xFF1A237E))
            Spacer(modifier = Modifier.width(8.dp))
            Text("N Perfume", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
        }

        // Icon Thông báo và Avatar Admin bên phải
        Row(verticalAlignment = Alignment.CenterVertically) {
            BadgedBox(badge = { Badge { Text("!") } }) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.Blue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar Admin (vẫn giữ ở Header như bạn yêu cầu)
                Icon(Icons.Default.AccountCircle, null, Modifier.size(32.dp), tint = Color.Gray)
                Text("ADMIN", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderCardItem(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Inventory2, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("Mã đơn: #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Người nhận: ${order.recipient}", fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                StatusLabel(order.status)
            }

            // Bên phải chỉ còn nút chat và menu, đã bỏ avatar khách hàng
            IconButton(onClick = {}) {
                Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(20.dp), tint = Color.Gray)
            }
            Icon(Icons.Default.MoreVert, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun StatusLabel(status: String) {
    val (bgColor, txtColor) = when (status) {
        "Chờ xác nhận" -> Color(0xFFE3F2FD) to Color(0xFF2196F3)
        "Đã xác nhận" -> Color(0xFFE8EAF6) to Color(0xFF3F51B5)
        "Đang giao"   -> Color(0xFFFFF9C4) to Color(0xFFFBC02D)
        "Đã Giao"     -> Color(0xFFE8F5E9) to Color(0xFF4CAF50)
        else          -> Color(0xFFFFEBEE) to Color(0xFFF44336)
    }

    Surface(color = bgColor, shape = RoundedCornerShape(50)) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp),
            fontSize = 10.sp,
            color = txtColor,
            fontWeight = FontWeight.Bold
        )
    }
}
// ... (Giữ nguyên các hàm OrderCardItem, StatusLabel, HomeHeader của bạn ở dưới)
// Bạn nhớ copy lại các hàm đó từ code cũ vào đây nhé
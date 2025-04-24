package com.example.adminlaptopstore.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.adminlaptopstore.navigation.BottomNavItem

@Composable
fun SettingsScreen(navController: NavController) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Cài Đặt",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingsItem(
                title = "Thống kê doanh thu",
                description = "Theo dõi báo cáo doanh thu của cửa hàng",
                icon = Icons.Default.BarChart,
                onClick = {
                    navController.navigate("revenue")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                title = "Quản lý người dùng",
                description = "Quản lý thông tin và quyền truy cập người dùng",
                icon = Icons.Default.People,
                onClick = {
                    navController.navigate("users")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                title = "Quản lý banner",
                description = "Cập nhật hình ảnh, nội dung quảng bá trên trang chính",
                icon = Icons.Default.Image,
                onClick = {
                    navController.navigate("banner")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            SettingsItem(
                title = "Quản lý kho hàng",
                description = "Theo dõi sản phẩm trong kho",
                icon = Icons.Default.Inventory,
                onClick = {
                    navController.navigate("warehouse")
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

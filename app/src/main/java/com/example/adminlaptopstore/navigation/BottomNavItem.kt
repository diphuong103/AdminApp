package com.example.adminlaptopstore.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object Category : BottomNavItem("Thể loại", Icons.Filled.List, "category")
    object Product : BottomNavItem("Sản phẩm", Icons.Filled.List, "product")
    object Order : BottomNavItem("Đơn hàng", Icons.Filled.ShoppingCart, "order")
    object Settings : BottomNavItem("Cài đặt", Icons.Filled.Settings, "settings")
    object Banner : BottomNavItem("Banner", Icons.Filled.Settings, "banner")
    object UserSc: BottomNavItem("Người dùng", Icons.Filled.Settings, "users")
    object Warehouse : BottomNavItem("Kho hàng", Icons.Filled.Settings, "warehouse")
    object Revenue : BottomNavItem("Doanh thu", Icons.Filled.Settings, "revenue")
}



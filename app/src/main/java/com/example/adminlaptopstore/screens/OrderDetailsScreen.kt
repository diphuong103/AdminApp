package com.example.adminlaptopstore.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adminlaptopstore.viewmodel.OrderViewModel
import java.util.*

@Composable
fun OrderDetailsScreen(
    orderId: String,
    viewModel: OrderViewModel = viewModel(),
    onNavigateBack: () -> Unit,
    onUpdateStatus: (String) -> Unit
) {
    val order = viewModel.getOrderById(orderId)
    val scaffoldState = rememberScaffoldState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết đơn hàng") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                backgroundColor = Color.White
            )
        },
        scaffoldState = scaffoldState
    ) { paddingValues ->
        if (order != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Order Info
                SectionTitle("Thông tin đơn hàng")
                DetailItem("Mã đơn hàng", if (order.orderId.isNotBlank()) order.orderId else order.productId)
                DetailItem("Sản phẩm", order.name)
                DetailItem("Số lượng", order.quantity.toString())
                DetailItem("Tổng tiền", formatCurrencyVND(order.totalPrice))
                DetailItem("Ngày đặt", order.date)

                // Status
                SectionTitle("Trạng thái")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    val statusColor = when(order.statusBill) {
                        "Đã hoàn thành" -> Color.Green
                        "Đang kiểm duyệt" -> Color.Blue
                        "Đang xử lý" -> Color.Cyan
                        "Đang giao hàng" -> Color.Magenta
                        "Đã hủy" -> Color.Red
                        else -> Color.Gray
                    }

                    Text(
                        text = order.statusBill,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = { onUpdateStatus(orderId) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3))
                    ) {
                        Text(text = "Cập nhật trạng thái", color = Color.White)
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Customer Info
                SectionTitle("Thông tin khách hàng")
                DetailItem("Họ và tên", "${order.lastName} ${order.firstName}")
                DetailItem("Email", order.email)
                DetailItem("Địa chỉ", order.address)
                DetailItem("Chi tiết", order.details_address)
                DetailItem("Thành phố", order.city)
                DetailItem("Mã bưu điện", order.postalCode)

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Payment Info
                SectionTitle("Thông tin thanh toán")
                DetailItem("Phương thức thanh toán", order.pay)
                DetailItem("Phương thức vận chuyển", order.transport)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Không tìm thấy thông tin đơn hàng",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(140.dp)
        )
        Text(text = value)
    }
}

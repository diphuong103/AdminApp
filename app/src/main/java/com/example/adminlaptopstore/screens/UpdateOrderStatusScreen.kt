package com.example.adminlaptopstore.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adminlaptopstore.viewmodel.OrderViewModel // Fixed import

@Composable
fun UpdateOrderStatusScreen(
    orderId: String,
    viewModel: OrderViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val order = viewModel.getOrderById(orderId)
    val statusOptions = listOf(
        "Đang kiểm duyệt",
        "Đang xử lý",
        "Đang giao hàng",
        "Đã hoàn thành",
        "Đã hủy"
    )

    var selectedStatus by remember { mutableStateOf(order?.statusBill ?: "Đang kiểm duyệt") }
    var isUpdating by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cập nhật trạng thái đơn hàng") },
                backgroundColor = Color.White
            )
        },
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (order != null) {
                Text(
                    text = "Chi tiết đơn hàng",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DetailRow("Mã đơn hàng", if (order.orderId.isNotBlank()) order.orderId else order.productId)
                DetailRow("Tên đơn hàng", order.name)
                DetailRow("Khách hàng", "${order.lastName} ${order.firstName}")

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Text(
                    text = "Chọn trạng thái mới",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Column {
                    statusOptions.forEach { status ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (status == selectedStatus),
                                    onClick = { selectedStatus = status }
                                )
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (status == selectedStatus),
                                onClick = { selectedStatus = status }
                            )
                            Text(
                                text = status,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                    ) {
                        Text("Hủy")
                    }

                    Button(
                        onClick = {
                            isUpdating = true
                            val id = if (order.orderId.isNotBlank()) order.orderId else order.productId
                            viewModel.updateOrderStatus(id, selectedStatus) { success ->
                                isUpdating = false
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        },
                        enabled = !isUpdating && selectedStatus != order.statusBill,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3))
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text("Lưu thay đổi")
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Không tìm thấy đơn hàng",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        Text(text = value)
    }
}
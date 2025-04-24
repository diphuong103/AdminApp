package com.example.adminlaptopstore.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.adminlaptopstore.viewmodel.OrderViewModel
import com.example.adminlaptopstore.model.OrderDataModels
import kotlinx.coroutines.launch


@Composable
fun OrderScreen(
    viewModel: OrderViewModel = viewModel(),
    onViewOrderDetails: (String) -> Unit,
    onUpdateOrderStatus: (String) -> Unit
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Filter orders based on search query
    val filteredOrders = orders.filter {
        it.name.contains(searchQuery.text, ignoreCase = true) ||
                "${it.lastName} ${it.firstName}".contains(searchQuery.text, ignoreCase = true)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm đơn hàng hoặc tên khách hàng") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (filteredOrders.isEmpty()) {
                Text(
                    text = "Không có đơn hàng nào",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filteredOrders) { order ->
                        OrderCard(
                            order = order,
                            onView = {
                                // Use orderId as priority, fall back to productId if needed
                                val id = if (order.orderId.isNotBlank()) order.orderId else order.productId
                                onViewOrderDetails(id)
                            },
                            onUpdateStatus = {
                                val id = if (order.orderId.isNotBlank()) order.orderId else order.productId
                                onUpdateOrderStatus(id)
                            },
                            onDelete = {
                                val id = if (order.orderId.isNotBlank()) order.orderId else order.productId
                                viewModel.deleteOrder(id) { success ->
                                    coroutineScope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            if (success) "Đã xóa đơn hàng" else "Không thể xóa đơn hàng"
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(
    order: OrderDataModels,
    onView: () -> Unit,
    onUpdateStatus: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = order.name, fontWeight = FontWeight.Bold)
                    Text(text = "Khách hàng: ${order.lastName} ${order.firstName}")
                    Text(text = "Tổng tiền: ${formatCurrencyVND(order.totalPrice)} VND")

                    val statusColor = when(order.statusBill) {
                        "Đã hoàn thành" -> Color.Green
                        "Đang kiểm duyệt" -> Color.Blue
                        "Đang xử lý" -> Color.Cyan
                        "Đang giao hàng" -> Color.Magenta
                        "Đã hủy" -> Color.Red
                        else -> Color.Gray
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Trạng thái: ")
                        Text(
                            text = order.statusBill,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row {
                    IconButton(onClick = onView) {
                        Icon(
                            Icons.Default.Visibility,
                            contentDescription = "Xem chi tiết",
                            tint = Color.Blue
                        )
                    }

                    TextButton(onClick = onUpdateStatus) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Cập nhật",
                            tint = Color(0xFF2196F3)
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Xóa đơn hàng",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

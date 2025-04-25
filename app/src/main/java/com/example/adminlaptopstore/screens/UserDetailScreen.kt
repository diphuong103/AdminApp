package com.example.adminlaptopstore.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.adminlaptopstore.ViewModel.UserViewModel
import com.example.adminlaptopstore.model.OrderDataModels
import com.example.adminlaptopstore.model.UserAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState(initial = null)
    val userOrders by userViewModel.userOrders.collectAsState(initial = emptyList())
    val isLoading by userViewModel.isLoading.collectAsState(initial = true)
    val errorMessage by userViewModel.errorMessage.collectAsState(initial = null)

    var editMode by remember { mutableStateOf(false) }
    var editedUser by remember { mutableStateOf<UserAddress?>(null) }

    // Load user data when screen is first shown
    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
    }

    // Update editedUser when currentUser changes
    LaunchedEffect(currentUser) {
        currentUser?.let {
            editedUser = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết người dùng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    if (editMode && editedUser != null) {
                        IconButton(onClick = {
                            editedUser?.let { user ->
                                userViewModel.updateUser(user) { success ->
                                    if (success) {
                                        editMode = false
                                    }
                                }
                            }
                        }) {
                            Icon(Icons.Default.Save, contentDescription = "Lưu")
                        }
                    } else {
                        IconButton(onClick = { editMode = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage ?: "Đã xảy ra lỗi",
                    color = Color.Red
                )
            }
        } else if (currentUser != null && editedUser != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // User Information Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Thông tin người dùng",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            if (editMode) {
                                // Editable fields
                                OutlinedTextField(
                                    value = editedUser?.firstName ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(firstName = it) },
                                    label = { Text("Họ") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.lastName ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(lastName = it) },
                                    label = { Text("Tên") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.phoneNumber ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(phoneNumber = it) },
                                    label = { Text("Số điện thoại") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.address ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(address = it) },
                                    label = { Text("Địa chỉ") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.city ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(city = it) },
                                    label = { Text("Thành phố") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.state ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(state = it) },
                                    label = { Text("Tỉnh") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.pinCode ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(pinCode = it) },
                                    label = { Text("Mã bưu điện") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )

                                OutlinedTextField(
                                    value = editedUser?.country ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(country = it) },
                                    label = { Text("Quốc gia") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                            } else {
                                // Display fields
                                InfoRow("ID", currentUser?.userId ?: "")
                                InfoRow("Họ và tên", "${currentUser?.firstName} ${currentUser?.lastName}")
                                InfoRow("Số điện thoại", currentUser?.phoneNumber ?: "")
                                InfoRow("Địa chỉ", currentUser?.address ?: "")
                                InfoRow("Thành phố", currentUser?.city ?: "")
                                InfoRow("Tỉnh", currentUser?.state ?: "")
                                InfoRow("Mã bưu điện", currentUser?.pinCode ?: "")
                                InfoRow("Quốc gia", currentUser?.country ?: "")
                            }
                        }
                    }
                }

                // User Orders Section
                item {
                    Text(
                        text = "Đơn hàng của người dùng",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    if (userOrders.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Người dùng chưa có đơn hàng nào")
                            }
                        }
                    }
                }

                // Order Items
                items(userOrders) { order ->
                    OrderItem(order)
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Không tìm thấy thông tin người dùng")
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Text(
            text = value,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
fun OrderItem(order: OrderDataModels) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${order.orderId.take(8)}...",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = order.date,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Tên sản phẩm: ${order.name}")
            Text(text = "Số lượng: ${order.quantity}")
            Text(text = "Tổng giá: ${formatCurrencyVND(order.totalPrice)} VND} đ")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Trạng thái đơn hàng: ${order.statusBill}",
                color = when(order.statusBill) {
                    "Đang kiểm duyệt" -> Color(0xFFFFA000)
                    "Đã xác nhận" -> Color(0xFF4CAF50)
                    "Đã hủy" -> Color.Red
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Phương thức thanh toán: ${order.pay}",
                fontSize = 14.sp
            )
            Text(
                text = "Phương thức vận chuyển: ${order.transport}",
                fontSize = 14.sp
            )
        }
    }
}
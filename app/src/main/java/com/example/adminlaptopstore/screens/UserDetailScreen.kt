package com.example.adminlaptopstore.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.transform.CircleCropTransformation
import com.example.adminlaptopstore.ViewModel.UserDataViewModel
import com.example.adminlaptopstore.model.OrderDataModels
import com.example.adminlaptopstore.model.UserData
import coil.request.ImageRequest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    navController: NavController,
    userViewModel: UserDataViewModel = viewModel(),
    onViewOrderDetails: (String) -> Unit
) {
    val currentUser by userViewModel.currentUser.collectAsState(initial = null)
    val userOrders by userViewModel.userOrders.collectAsState(initial = emptyList())
    val isLoading by userViewModel.isLoading.collectAsState(initial = true)
    val errorMessage by userViewModel.errorMessage.collectAsState(initial = null)

    var editMode by remember { mutableStateOf(false) }
    var editedUser by remember { mutableStateOf<UserData?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            editedUser = editedUser?.copy(profileImage = it.toString())
        }
    }

    LaunchedEffect(userId) {
        userViewModel.getUserByIdData(userId)
    }

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
                                userViewModel.updateUserData(user) { success ->
                                    if (success) editMode = false
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
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Thông tin người dùng",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Avatar
                            Image(
                                painter = rememberAsyncImagePainter(
                                    coil.request.ImageRequest.Builder(LocalContext.current)
                                        .data(selectedImageUri ?: editedUser?.profileImage ?: "https://via.placeholder.com/150")
                                        .transformations(CircleCropTransformation())
                                        .build()
                                ),
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .clickable(enabled = editMode) {
                                        imagePickerLauncher.launch("image/*")
                                    }
                            )

                            if (editMode) {
                                Text(
                                    text = "Nhấn vào ảnh để thay đổi",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            if (editMode) {
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
                                    value = editedUser?.email ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(email = it) },
                                    label = { Text("Email") },
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
                                    value = editedUser?.password ?: "",
                                    onValueChange = { editedUser = editedUser?.copy(password = it) },
                                    label = { Text("Mật khẩu") },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                )
                            } else {
                                InfoRow("ID", currentUser?.userId ?: "")
                                InfoRow("Họ và tên", "${currentUser?.firstName} ${currentUser?.lastName}")
                                InfoRow("Email", currentUser?.email ?: "")
                                InfoRow("Số điện thoại", currentUser?.phoneNumber ?: "")
                                InfoRow("Địa chỉ", currentUser?.address ?: "")
                            }
                        }
                    }
                }

                // Order Section
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
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

                items(userOrders) { order ->
                    OrderItem(
                        order = order,
                        onView = {
                            val id = if (order.orderId.isNotBlank()) order.orderId else order.productId
                            onViewOrderDetails(id)
                        }
                    )
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
        Text(text = label, fontWeight = FontWeight.Medium, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun OrderItem(order: OrderDataModels, onView: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Order #${order.orderId.take(8)}...", fontWeight = FontWeight.Bold)
                Text(text = order.date, color = Color.Gray)
                IconButton(onClick = onView) {
                    Icon(Icons.Default.EditNote, contentDescription = "Xem chi tiết", tint = Color.Blue)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Sản phẩm: ${order.name}")
            Text("Số lượng: ${order.quantity}")
            Text("Tổng giá: ${formatCurrencyVND(order.totalPrice)} VND")

            Spacer(modifier = Modifier.height(4.dp))

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "Trạng thái đơn hàng: ${order.statusBill}",
                    color = when (order.statusBill) {
                        "Đang kiểm duyệt" -> Color(0xFFFFA000)
                        "Đã xác nhận" -> Color(0xFF4CAF50)
                        "Đã hủy" -> Color.Red
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Phương thức thanh toán: ${order.pay}", fontSize = 14.sp)
                Text("Phương thức vận chuyển: ${order.transport}", fontSize = 14.sp)
            }
        }
    }
}

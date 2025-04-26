package com.example.adminlaptopstore.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.adminlaptopstore.ViewModel.UserViewModel
import com.example.adminlaptopstore.model.UserAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val users by userViewModel.users.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var showDeleteDialog by remember { mutableStateOf<UserAddress?>(null) }

    LaunchedEffect(key1 = true) {
        userViewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý người dùng") },
                actions = {
                    IconButton(onClick = { userViewModel.loadAllUsers() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Làm mới")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm người dùng") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error message with retry option
            errorMessage?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = it,
                                color = Color.Red
                            )
                        }
                        Button(onClick = { userViewModel.retryLastOperation() }) {
                            Text("Làm mới")
                        }
                    }
                }
            }

            // User List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val filteredUsers = users.filter {
                        val fullName = "${it.firstName} ${it.lastName}".lowercase()
                        fullName.contains(searchQuery.text.lowercase()) ||
                                it.phoneNumber.contains(searchQuery.text)
                    }

                    if (filteredUsers.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (users.isEmpty()) {
                                    Text("Không tìm thấy người dùng", fontSize = 16.sp, color = Color.Gray)
                                } else {
                                    Text("Không có người dùng phù hợp", fontSize = 16.sp, color = Color.Gray)
                                }
                            }
                        }
                    }

                    items(filteredUsers) { user ->
                        UserListItem(
                            user = user,
                            onViewDetail = {
                                navController.navigate("user_detail/${user.userId}")
                            },
                            onDeleteUser = {
                                showDeleteDialog = user
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { user ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa người dùng không? ${user.firstName} ${user.lastName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        userViewModel.deleteUser(user.userId) { success ->
                            showDeleteDialog = null
                        }
                    }
                ) {
                    Text("Xác nhận")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Hủy bỏ")
                }
            }
        )
    }
}

@Composable
fun UserListItem(
    user: UserAddress,
    onViewDetail: () -> Unit,
    onDeleteUser: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${user.firstName} ${user.lastName}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = user.phoneNumber, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${user.address}, ${user.city}, ${user.state}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onViewDetail) {
                Icon(Icons.Default.Info, contentDescription = "Xem chi tiết")
            }

            IconButton(onClick = onDeleteUser) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Xóa người dùng",
                    tint = Color.Red
                )
            }
        }
    }
}
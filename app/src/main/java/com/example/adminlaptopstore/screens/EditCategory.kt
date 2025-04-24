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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.adminlaptopstore.viewmodel.CategoryViewModel
import kotlinx.coroutines.launch


@Composable
fun EditCategoryScreen(
    categoryId: String,
    onEditComplete: () -> Unit,
    navController: NavController,
    viewModel: CategoryViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val category = categories.find { it.id == categoryId }
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

// Trạng thái cho các trường biểu mẫu
    var categoryName by remember { mutableStateOf("") }
    var createdBy by remember { mutableStateOf("") }
    var categoryImage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Khởi tạo trạng thái với dữ liệu danh mục khi có sẵn
    LaunchedEffect(category) {
        category?.let {
            categoryName = it.name
            createdBy = it.createBy
            categoryImage = it.categoryImage
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thể loại") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (category == null) {
            // Hiển thị trạng thái tải hoặc lỗi nếu không tìm thấy danh mục
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Edit form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Tên thể loại") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = createdBy,
                    onValueChange = { createdBy = it },
                    label = { Text("Người tạo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = categoryImage,
                    onValueChange = { categoryImage = it },
                    label = { Text("Đường dẫn hình ảnh") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Có thể thêm chức năng upload hình ảnh thực tế ở đây

                Button(
                    onClick = {
                        if (categoryName.isBlank()) {
                            coroutineScope.launch {
                                scaffoldState.snackbarHostState.showSnackbar("Vui lòng nhập tên thể loại")
                            }
                            return@Button
                        }

                        isLoading = true
                        val updatedCategory = category.copy(
                            name = categoryName,
                            createBy = createdBy,
                            categoryImage = categoryImage
                        )

                        viewModel.updateCategory(updatedCategory) { success ->
                            isLoading = false
                            if (success) {
                                onEditComplete()
                            } else {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Cập nhật thất bại, vui lòng thử lại")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colors.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Cập nhật thể loại")
                    }
                }
            }
        }
    }
}
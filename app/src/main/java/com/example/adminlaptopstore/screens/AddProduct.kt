package com.example.adminlaptopstore.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopstore.model.ProductDataModels
import com.example.adminlaptopstore.viewmodel.CategoryViewModel
import com.example.adminlaptopstore.viewmodel.ProductViewModel

@Composable
fun AddProductScreen(
    productViewModel: ProductViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    categoryViewModel: CategoryViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isLoading by productViewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var shortDesc by remember { mutableStateOf("") }
    var longDesc by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("0") }
    var target by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Còn hàng") }
    var imageUrl by remember { mutableStateOf("") }

    // Get categories from CategoryViewModel instead of ProductViewModel
    val categories by categoryViewModel.categories.collectAsState()
    val statusOptions = listOf("Còn hàng", "Hết hàng")


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Thêm sản phẩm mới",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên sản phẩm") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = shortDesc,
                    onValueChange = { shortDesc = it },
                    label = { Text("Mô tả ngắn") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                OutlinedTextField(
                    value = longDesc,
                    onValueChange = { longDesc = it },
                    label = { Text("Mô tả chi tiết") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    maxLines = 5
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Số lượng") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Đối tượng") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                // Category Dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Thể loại",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                        color = MaterialTheme.colors.primary
                    )

                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f)),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (category.isEmpty()) "Chọn thể loại" else category,
                                color = if (category.isEmpty()) Color.Gray else Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Arrow"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        if (categories.isEmpty()) {
                            DropdownMenuItem(onClick = {}) {
                                Text("Không có dữ liệu thể loại")
                            }
                        } else {
                            categories.forEach { item ->
                                DropdownMenuItem(onClick = {
                                    category = item.name
                                    categoryExpanded = false
                                }) {
                                    Text(text = item.name)
                                }
                            }
                        }
                    }
                }

                // Status Dropdown
                var statusExpanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "Trạng thái",
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
                        color = MaterialTheme.colors.primary
                    )

                    OutlinedButton(
                        onClick = { statusExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f)),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status,
                                color = Color.Black
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown Arrow"
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(onClick = {
                                status = option
                                statusExpanded = false
                            }) {
                                Text(text = option)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL hình ảnh") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                // Image preview
                if (imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Product Image Preview",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
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
                    if (name.isBlank() || price.isBlank() || imageUrl.isBlank()) {
                        Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val quantityInt = quantity.toIntOrNull() ?: 0

                    val newProduct = ProductDataModels(
                        productId = "", // ID will be set by Firestore
                        name = name,
                        price = price.toDouble(),
                        short_desc = shortDesc,
                        long_desc = longDesc,
                        quantity = quantityInt,
                        target = target,
                        category = category,
                        status = status,
                        image = imageUrl,
                        date = System.currentTimeMillis(),
                        createBy = "" // Can be set based on current user if available
                    )

                    productViewModel.addProduct(newProduct) { success ->
                        if (success) {
                            Toast.makeText(context, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Thêm sản phẩm thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF673AB7))
            ) {
                Text("Thêm", color = Color.White)
            }
        }
    }
}
package com.example.adminlaptopstore.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopstore.viewmodel.CategoryViewModel
import com.example.adminlaptopstore.viewmodel.ProductViewModel



@Composable
fun EditProductScreen(
    productId: String,
    productViewModel: ProductViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val products by productViewModel.products.collectAsState()
    val product = products.find { it.productId == productId }
    val isLoading by productViewModel.isLoading.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()
    val statusOptions = listOf("Còn hàng", "Hết hàng")

    // Check if product found
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy sản phẩm")
        }
        return
    }

    var name by remember { mutableStateOf(product.name) }
    var price by remember { mutableStateOf(product.price.toString()) }
    var shortDesc by remember { mutableStateOf(product.short_desc) }
    var longDesc by remember { mutableStateOf(product.long_desc) }
    var quantity by remember { mutableStateOf(product.quantity.toString()) }
    var target by remember { mutableStateOf(product.target) }
    var category by remember { mutableStateOf(product.category) }
    var status by remember { mutableStateOf(product.status) }
    var imageUrl by remember { mutableStateOf(product.image) }

    // Auto-update status based on quantity
    LaunchedEffect(quantity) {
        val quantityValue = quantity.toIntOrNull() ?: 0
        if (quantityValue <= 0) {
            status = "Hết hàng"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chỉnh sửa sản phẩm",
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
//                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number)
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
                    onValueChange = {
                        quantity = it
                        // Auto update status when quantity changes to 0
                        val quantityInt = it.toIntOrNull() ?: 0
                        if (quantityInt <= 0) {
                            status = "Hết hàng"
                        } else if (status == "Hết hàng") {
                            // Only auto-change to in stock if it was out of stock
                            status = "Còn hàng"
                        }
                    },
                    label = { Text("Số lượng") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
//                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number)
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
                                text = category,
                                color = Color.Black
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
                                color = if (status == "Hết hàng") Color.Red else Color.Black
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

                                // If changing to "Còn hàng" but quantity is 0, warn user
                                if (option == "Còn hàng" && (quantity.toIntOrNull() ?: 0) <= 0) {
                                    Toast.makeText(
                                        context,
                                        "Cảnh báo: Số lượng đang là 0, nên cập nhật số lượng",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                                statusExpanded = false
                            }) {
                                Text(
                                    text = option,
                                    color = if (option == "Hết hàng") Color.Red else Color.Black
                                )
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
                    val quantityInt = quantity.toIntOrNull() ?: 0

                    // Final check to ensure status is correct based on quantity
                    val finalStatus = if (quantityInt <= 0) "Hết hàng" else status

                    val updatedProduct = product.copy(
                        name = name,
                        price = price.toDoubleOrNull() ?: product.price,
                        short_desc = shortDesc,
                        long_desc = longDesc,
                        quantity = quantityInt,
                        target = target,
                        category = category,
                        status = finalStatus,
                        image = imageUrl
                    )

                    productViewModel.updateProduct(updatedProduct) { success ->
                        if (success) {
                            Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF673AB7))
            ) {
                Text("Lưu", color = Color.White)
            }
        }
    }
}
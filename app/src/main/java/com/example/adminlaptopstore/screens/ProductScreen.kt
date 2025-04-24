package com.example.adminlaptopstore.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopstore.model.ProductDataModels
import com.example.adminlaptopstore.viewmodel.ProductViewModel

@Composable
fun ProductScreen(
    viewModel: ProductViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onAddProductClick: () -> Unit,
    onEditProductClick: (ProductDataModels) -> Unit // ← Thêm tham số này
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val products by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddProductClick() },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm sản phẩm")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Tìm kiếm") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn {
                    items(products.size) { index ->
                        val product = products[index]
                        ProductCard(
                            product = product,
                            onEdit = {
                                onEditProductClick(product) // ← Gọi callback edit
                            },
                            onDelete = {
                                viewModel.deleteProduct(product.productId) { success ->
                                    // Xử lý nếu cần
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

fun formatCurrencyVND(amount: Double): String {
    return String.format("%,.0f", amount).replace(",", ".")
}


@Composable
fun ProductCard(
    product: ProductDataModels,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hình ảnh sản phẩm
            Image(
                painter = rememberAsyncImagePainter(product.image),
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(60.dp)
                    .padding(end = 16.dp)
            )

            // Thông tin sản phẩm
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Giá: ${formatCurrencyVND(product.price)} VND",
                    style = MaterialTheme.typography.body2
                )

                Text(
                    text = "Thể loại: ${product.category}",
                    style = MaterialTheme.typography.body2
                )
                Text(
                    text = "Số lượng: ${product.quantity}",
                    style = MaterialTheme.typography.body2
                )
            }

            // Icon chỉnh sửa và xóa
            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Blue)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                }
            }
        }
    }
}
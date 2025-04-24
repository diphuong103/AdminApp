package com.example.adminlaptopstore.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.adminlaptopstore.model.CategoryDataModels
import com.example.adminlaptopstore.firebase.FirebaseManager

@Composable
fun AddCategoryScreen(
    onAddCategory: (CategoryDataModels) -> Unit,
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var createBy by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Thêm Thể Loại Mới", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tên thể loại") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = createBy,
            onValueChange = { createBy = it },
            label = { Text("Người tạo") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chọn hình ảnh")
        }

        imageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (name.isNotEmpty() && createBy.isNotEmpty()) {
                    val category = CategoryDataModels(
                        name = name,
                        createBy = createBy,
                        categoryImage = imageUri?.toString() ?: ""
                    )
                    onAddCategory(category)

                    // Reset form
                    name = ""
                    createBy = ""
                    imageUri = null
                } else {
                    Toast.makeText(context, "Tên thể loại và Người tạo không được để trống.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Thêm thể loại")
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.align(Alignment.Start)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Trở về"
            )
        }
    }
}

@Composable
fun AddCategoryPage(navController: NavController) {
    val context = LocalContext.current
    var showToastMessage by remember { mutableStateOf<String?>(null) }

    AddCategoryScreen(onAddCategory = { category ->
        FirebaseManager.addCategory(category) { success ->
            showToastMessage = if (success) {
                navController.popBackStack()
                "Thể loại đã được thêm thành công."
            } else {
                "Lỗi khi thêm thể loại."
            }
        }
    }, navController = navController)

    // Hiển thị toast nếu có message
    LaunchedEffect(showToastMessage) {
        showToastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            showToastMessage = null
        }
    }
}


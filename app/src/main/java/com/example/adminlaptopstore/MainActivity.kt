package com.example.adminlaptopstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.screens.MainScreen
import com.example.adminlaptopstore.ui.theme.AdminLaptopStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminLaptopStoreTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Chuyển hướng khi đăng xuất
                    MainScreen(onLogout = {

                    })
                }
            }
        }
    }
}

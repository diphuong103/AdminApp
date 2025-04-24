package com.example.adminlaptopstore.model

import java.text.SimpleDateFormat
import java.util.Locale.getDefault

data class OrderDataModels(
    val name: String = "",
    val productId: String = "",
    val userId: String = "",
    val quantity: Int = 0,
    val totalPrice: Double = 0.0,
    val email: String = "",
    val address: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val details_address: String = "",
    val city: String = "",
    val postalCode: String = "",
    val transport: String = "",
    val pay: String = "",
    val statusBill: String = "Đang kiểm duyệt",
    val date: String = SimpleDateFormat("dd/MM/yyyy", getDefault()).format(System.currentTimeMillis()),
    val orderId: String = ""
)
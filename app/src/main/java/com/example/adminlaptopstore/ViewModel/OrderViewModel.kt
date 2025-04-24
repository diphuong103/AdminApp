package com.example.adminlaptopstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.model.OrderDataModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val _orders = MutableStateFlow<List<OrderDataModels>>(emptyList())
    val orders: StateFlow<List<OrderDataModels>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                FirebaseManager.getOrders().collect { ordersList ->
                    _orders.value = ordersList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                // Handle error
                _isLoading.value = false
            }
        }
    }

    fun getOrderById(orderId: String): OrderDataModels? {
        // Look for both orderId and productId to ensure compatibility
        return orders.value.find { it.orderId == orderId || it.productId == orderId }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, onComplete: (Boolean) -> Unit) {
        FirebaseManager.updateOrderStatus(orderId, newStatus) { isSuccessful ->
            onComplete(isSuccessful)
        }
    }

    fun deleteOrder(orderId: String, onComplete: (Boolean) -> Unit) {
        FirebaseManager.deleteOrder(orderId, onComplete)
    }
}
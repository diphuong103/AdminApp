package com.example.adminlaptopstore.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.model.CategoryDataModels
import com.example.adminlaptopstore.model.ProductDataModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.junit.experimental.categories.Category

class ProductViewModel : ViewModel() {
    private val _products = MutableStateFlow<List<ProductDataModels>>(emptyList())
    val products: StateFlow<List<ProductDataModels>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    init {
        loadProducts()
    }

    private val _categories = MutableStateFlow<List<CategoryDataModels>>(emptyList())
    val categories: StateFlow<List<CategoryDataModels>> = _categories.asStateFlow()

    private fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseManager.getProducts().collect { productList ->
                _products.value = productList
                _isLoading.value = false
            }
        }
    }

    fun addProduct(product: ProductDataModels, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        FirebaseManager.addProduct(product) { success ->
            _isLoading.value = false
            if (success) {
                // Reload products to get the new product with its ID
                loadProducts()
            }
            onComplete(success)
        }
    }

    fun deleteProduct(productId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        FirebaseManager.deleteProduct(productId) { success ->
            _isLoading.value = false
            if (success) {
                // Remove from local list immediately for better UX
                _products.value = _products.value.filter { it.productId != productId }
            }
            onComplete(success)
        }
    }

    fun updateProduct(product: ProductDataModels, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        FirebaseManager.updateProduct(product) { success ->
            _isLoading.value = false
            if (success) {
                // Update local list immediately for better UX
                _products.value = _products.value.map {
                    if (it.productId == product.productId) product else it
                }
            }
            onComplete(success)
        }
    }
}
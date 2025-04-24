package com.example.adminlaptopstore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.model.CategoryDataModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val _categories = MutableStateFlow<List<CategoryDataModels>>(emptyList())
    val categories: StateFlow<List<CategoryDataModels>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseManager.getCategories().collect { categoryList ->
                _categories.value = categoryList
                _isLoading.value = false
            }
        }
    }


    fun deleteCategory(categoryId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        FirebaseManager.deleteCategory(categoryId) { success ->
            _isLoading.value = false
            onComplete(success)
        }
    }

    fun updateCategory(category: CategoryDataModels, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        FirebaseManager.updateCategory(category) { success ->
            _isLoading.value = false
            onComplete(success)
        }
    }
}
package com.example.adminlaptopstore.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.model.OrderDataModels
import com.example.adminlaptopstore.model.UserAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class UserViewModel : ViewModel() {
    private val _users = MutableStateFlow<List<UserAddress>>(emptyList())
    val users: StateFlow<List<UserAddress>> = _users.asStateFlow()

    private val _currentUser = MutableStateFlow<UserAddress?>(null)
    val currentUser: StateFlow<UserAddress?> = _currentUser.asStateFlow()

    private val _userOrders = MutableStateFlow<List<OrderDataModels>>(emptyList())
    val userOrders: StateFlow<List<OrderDataModels>> = _userOrders.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    init {
        loadAllUsers()
    }


    // Load all users
    fun loadAllUsers() {
        _isLoading.value = true
        _errorMessage.value = null

        FirebaseManager.getAllUsers(
            onSuccess = { usersList ->
                _users.value = usersList
                _isLoading.value = false
            },
            onFailure = { exception ->
                _errorMessage.value = "Failed to load users: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Get user by ID
    fun getUserById(userId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        FirebaseManager.getUserById(
            userId = userId,
            onSuccess = { user ->
                user?.let {
                    _currentUser.value = it
                    loadUserOrders(userId)
                } ?: run {
                    _errorMessage.value = "User not found"
                    _isLoading.value = false
                }
            },
            onFailure = { exception ->
                _errorMessage.value = "Failed to load user: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Update user
    fun updateUser(user: UserAddress, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        FirebaseManager.updateUser(
            user = user,
            onSuccess = {
                _currentUser.value = user
                // Also update the user in the users list
                _users.value = _users.value.map {
                    if (it.userId == user.userId) user else it
                }
                _isLoading.value = false
                onComplete(true)
            },
            onFailure = { exception ->
                _errorMessage.value = "Failed to update user: ${exception.message}"
                _isLoading.value = false
                onComplete(false)
            }
        )
    }

    // Delete user
    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        _isLoading.value = true
        _errorMessage.value = null

        FirebaseManager.deleteUser(
            userId = userId,
            onSuccess = {
                // Remove user from the list
                _users.value = _users.value.filter { it.userId != userId }
                _isLoading.value = false
                onComplete(true)
            },
            onFailure = { exception ->
                _errorMessage.value = "Failed to delete user: ${exception.message}"
                _isLoading.value = false
                onComplete(false)
            }
        )
    }

    // Load user orders
    private fun loadUserOrders(userId: String) {
        FirebaseManager.getUserOrders(
            userId = userId,
            onSuccess = { ordersList ->
                _userOrders.value = ordersList
                _isLoading.value = false
            },
            onFailure = { exception ->
                _errorMessage.value = "Failed to load orders: ${exception.message}"
                _isLoading.value = false
            }
        )
    }

    // Function to retry latest failed operation
    fun retryLastOperation() {
        _currentUser.value?.userId?.let { userId ->
            getUserById(userId)
        } ?: loadAllUsers()
    }
}
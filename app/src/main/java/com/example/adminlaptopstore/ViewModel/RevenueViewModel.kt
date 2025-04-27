package com.example.adminlaptopstore.ViewModel

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adminlaptopstore.firebase.FirebaseManager
import com.example.adminlaptopstore.model.OrderDataModels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class RevenueViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _deliveredOrders = MutableStateFlow<List<OrderDataModels>>(emptyList())
    val deliveredOrders: StateFlow<List<OrderDataModels>> = _deliveredOrders.asStateFlow()

    private val _totalRevenue = MutableStateFlow(0.0)
    val totalRevenue: StateFlow<Double> = _totalRevenue.asStateFlow()

    private val _orderCount = MutableStateFlow(0)
    val orderCount: StateFlow<Int> = _orderCount.asStateFlow()

    private val _revenueData = MutableStateFlow<Map<String, Double>>(emptyMap())
    val revenueData: StateFlow<Map<String, Double>> = _revenueData.asStateFlow()

    private var allOrders = emptyList<OrderDataModels>()

    init {
        loadAllOrders()
    }

    private fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseManager.getOrdersRevenue().collect { orders ->
                    allOrders = orders.filter { it.statusBill == "Đã hoàn thành" }
                    _deliveredOrders.value = allOrders
                    _orderCount.value = allOrders.size
                    _totalRevenue.value = allOrders.sumOf { it.totalPrice }

                    // Set default to current month
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val now = LocalDate.now()
                        val startOfMonth = now.withDayOfMonth(1)
                        val endOfMonth = now.withDayOfMonth(now.month.length(now.isLeapYear))

                        val startDate = startOfMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        val endDate = endOfMonth.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        loadDailyRevenueData(startDate, endDate)
                    } else {
                        // Fallback for older Android versions
                        val calendar = Calendar.getInstance()
                        val currentYear = calendar.get(Calendar.YEAR)
                        val currentMonth = calendar.get(Calendar.MONTH)

                        calendar.set(currentYear, currentMonth, 1)
                        val startDate = "%02d/%02d/%d".format(
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.YEAR)
                        )

                        calendar.set(currentYear, currentMonth, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                        val endDate = "%02d/%02d/%d".format(
                            calendar.get(Calendar.DAY_OF_MONTH),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.YEAR)
                        )

                        val filteredOrders = filterOrdersByDateRange(allOrders, startDate, endDate)
                        processDailyRevenueData(filteredOrders)
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyDateFilter(startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                FirebaseManager.getDeliveredOrdersByDateRange(startDate, endDate).collect { orders ->
                    _deliveredOrders.value = orders
                    _orderCount.value = orders.size
                    _totalRevenue.value = orders.sumOf { it.totalPrice }
                }

                loadDailyRevenueData(startDate, endDate)
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun applyYearFilter(year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Filter orders by year
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val filtered = allOrders.filter { order ->
                    try {
                        val orderDate = LocalDate.parse(order.date, formatter)
                        orderDate.year == year
                    } catch (e: Exception) {
                        false
                    }
                }

                _deliveredOrders.value = filtered
                _orderCount.value = filtered.size
                _totalRevenue.value = filtered.sumOf { it.totalPrice }

                loadMonthlyRevenueData(year)
            } finally {
                _isLoading.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDailyRevenueData(startDate: String, endDate: String) {
        viewModelScope.launch {
            FirebaseManager.getDailyRevenueData(startDate, endDate).collect { data ->
                _revenueData.value = data
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadMonthlyRevenueData(year: Int) {
        viewModelScope.launch {
            FirebaseManager.getMonthlyRevenueData(year).collect { data ->
                _revenueData.value = data
            }
        }
    }

    // For API level compatibility
    private fun filterOrdersByDateRange(orders: List<OrderDataModels>, startDate: String, endDate: String): List<OrderDataModels> {
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val startTimestamp = try {
            dateFormat.parse(startDate)?.time ?: 0
        } catch (e: Exception) {
            0
        }

        val endTimestamp = try {
            dateFormat.parse(endDate)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }

        return orders.filter { order ->
            try {
                val orderTimestamp = dateFormat.parse(order.date)?.time ?: 0
                orderTimestamp in startTimestamp..endTimestamp
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun processDailyRevenueData(orders: List<OrderDataModels>) {
        val data = orders.groupBy { it.date }
            .mapValues { it.value.sumOf { order -> order.totalPrice } }
        _revenueData.value = data
    }

    // Handle for API level < 26
    fun applyDateFilterCompat(startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val filtered = filterOrdersByDateRange(allOrders, startDate, endDate)
                _deliveredOrders.value = filtered
                _orderCount.value = filtered.size
                _totalRevenue.value = filtered.sumOf { it.totalPrice }
                processDailyRevenueData(filtered)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun applyYearFilterCompat(year: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val filtered = allOrders.filter { order ->
                    try {
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(order.date) ?: Date()
                        calendar.get(Calendar.YEAR) == year
                    } catch (e: Exception) {
                        false
                    }
                }

                _deliveredOrders.value = filtered
                _orderCount.value = filtered.size
                _totalRevenue.value = filtered.sumOf { it.totalPrice }

                // Process monthly data
                val monthlyData = mutableMapOf<String, Double>()
                for (month in 1..12) {
                    monthlyData["Tháng $month/$year"] = 0.0
                }

                filtered.forEach { order ->
                    try {
                        val calendar = Calendar.getInstance()
                        calendar.time = dateFormat.parse(order.date) ?: Date()
                        val month = calendar.get(Calendar.MONTH) + 1
                        val monthKey = "Tháng $month/$year"

                        monthlyData[monthKey] = monthlyData.getOrDefault(monthKey, 0.0) + order.totalPrice
                    } catch (e: Exception) {
                        // Skip invalid dates
                    }
                }

                _revenueData.value = monthlyData
            } finally {
                _isLoading.value = false
            }
        }
    }

}
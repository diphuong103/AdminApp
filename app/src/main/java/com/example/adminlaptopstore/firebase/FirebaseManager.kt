package com.example.adminlaptopstore.firebase

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.adminlaptopstore.firebase.FirebaseManager.firestore
import com.example.adminlaptopstore.model.CategoryDataModels
import com.example.adminlaptopstore.model.OrderDataModels
import com.example.adminlaptopstore.model.ProductDataModels
import com.example.adminlaptopstore.model.UserAddress
import com.example.adminlaptopstore.model.UserData
import com.example.adminlaptopstore.navigation.BottomNavItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object FirebaseManager {
    val firestore = FirebaseFirestore.getInstance()


    // Thêm thể loại mới
    fun addCategory(category: CategoryDataModels, onResult: (Boolean) -> Unit) {
        val docRef = firestore.collection("Categories").document()
        val categoryWithId = category.copy(id = docRef.id)

        // Tạo map với tên trường khớp với những gì mong đợi trong Firestore
        val categoryMap = hashMapOf(
            "Loại" to categoryWithId.name,
            "Người thêm" to categoryWithId.createBy,
            "date" to System.currentTimeMillis(),
            "categoryImage" to categoryWithId.categoryImage
        )

        docRef.set(categoryMap)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Lấy danh sách tất cả thể loại
    fun getCategories(): Flow<List<CategoryDataModels>> = callbackFlow {
        val listenerRegistration = firestore.collection("Categories")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { doc ->
                        try {
                            // Tạo CategoryDataModels từ document với mapping rõ ràng
                            val id = doc.id
                            val name = doc.getString("Loại") ?: ""  // Lấy từ trường "Loại"
                            val createBy =
                                doc.getString("Người thêm") ?: ""  // Lấy từ trường "Người thêm"
                            val date = doc.getLong("date") ?: System.currentTimeMillis()
                            val categoryImage = doc.getString("categoryImage") ?: ""

                            CategoryDataModels(
                                id = id,
                                name = name,
                                date = date,
                                createBy = createBy,
                                categoryImage = categoryImage
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(categories)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }


    // Xóa thể loại theo ID
    fun deleteCategory(categoryId: String, onResult: (Boolean) -> Unit) {
        firestore.collection("Categories").document(categoryId)
            .delete()
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // Cập nhật thông tin thể loại
    fun updateCategory(category: CategoryDataModels, onResult: (Boolean) -> Unit) {
        // Tạo map với các trường dữ liệu theo đúng tên field trong Firestore
        val categoryMap = hashMapOf(
            "Loại" to category.name,
            "Người thêm" to category.createBy,
            "date" to category.date,
            "categoryImage" to category.categoryImage
        )

        firestore.collection("Categories").document(category.id)
            .set(categoryMap, SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }

    }

    //////////////////////////////////////////
    private val productsRef = firestore.collection("Products")

    // Thêm sản phẩm mới
    fun addProduct(product: ProductDataModels, onComplete: (Boolean) -> Unit) {
        val newDoc = productsRef.document()
        product.productId = newDoc.id
        newDoc.set(product)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    // Cập nhật sản phẩm
    fun updateProduct(product: ProductDataModels, onComplete: (Boolean) -> Unit) {
        if (product.productId.isBlank()) {
            onComplete(false)
            return
        }
        productsRef.document(product.productId)
            .set(product)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    // Xóa sản phẩm
    fun deleteProduct(productId: String, onComplete: (Boolean) -> Unit) {
        if (productId.isBlank()) {
            onComplete(false)
            return
        }
        productsRef.document(productId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun getProducts(): Flow<List<ProductDataModels>> = callbackFlow {
        val listenerRegistration = firestore.collection("Products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { doc ->
                        try {
                            // Lấy dữ liệu từ Firestore
                            val productId = doc.id
                            val name = doc.getString("name") ?: ""


                            val priceValue = doc.get("price")
                            val price = when (priceValue) {
                                is String -> priceValue.toDoubleOrNull() ?: 0.0
                                is Number -> priceValue.toDouble()
                                else -> 0.0
                            }


                            val quantityValue = doc.get("quantity")
                            val quantity = when (quantityValue) {
                                is String -> quantityValue.toIntOrNull() ?: 0
                                is Number -> quantityValue.toInt()
                                else -> 0
                            }


                            val shortDesc = doc.getString("short_desc") ?: ""
                            val longDesc = doc.getString("long_desc") ?: ""
                            val target = doc.getString("target") ?: ""
                            val status = doc.getString("status") ?: ""
                            val category = doc.getString("category") ?: ""
                            val image = doc.getString("image") ?: ""
                            val date = doc.getLong("date") ?: System.currentTimeMillis()
                            val createBy = doc.getString("createBy") ?: ""

                            // Tạo ProductDataModels từ dữ liệu
                            ProductDataModels(
                                productId = productId,
                                name = name,
                                price = price,
                                short_desc = shortDesc,
                                long_desc = longDesc,
                                quantity = quantity,
                                target = target,
                                category = category,
                                status = status,
                                image = image,
                                date = date,
                                createBy = createBy
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(products)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }


///// oder ////

    // Order related functions in FirebaseManager.kt

    fun getOrders(): Flow<List<OrderDataModels>> = callbackFlow {
        val listenerRegistration = firestore.collection("Orders")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val orders = snapshot.documents.mapNotNull { doc ->
                        try {
                            // Map Firestore data to OrderDataModels
                            val orderId = doc.id
                            val name = doc.getString("name") ?: ""
                            val productId = doc.getString("productId") ?: ""
                            val userId = doc.getString("userId") ?: ""

                            val quantityValue = doc.get("quantity")
                            val quantity = when (quantityValue) {
                                is String -> quantityValue.toIntOrNull() ?: 0
                                is Number -> quantityValue.toInt()
                                else -> 0
                            }

                            val totalPriceValue = doc.get("totalPrice")
                            val totalPrice = when (totalPriceValue) {
                                is String -> totalPriceValue.toDoubleOrNull() ?: 0.0
                                is Number -> totalPriceValue.toDouble()
                                else -> 0.0
                            }

                            val email = doc.getString("email") ?: ""
                            val address = doc.getString("address") ?: ""
                            val firstName = doc.getString("firstName") ?: ""
                            val lastName = doc.getString("lastName") ?: ""
                            val details_address = doc.getString("details_address") ?: ""
                            val city = doc.getString("city") ?: ""
                            val postalCode = doc.getString("postalCode") ?: ""
                            val transport = doc.getString("transport") ?: ""
                            val pay = doc.getString("pay") ?: ""
                            val statusBill = doc.getString("statusBill") ?: "Đang kiểm duyệt"
                            val date = doc.getString("date") ?: ""

                            OrderDataModels(
                                name = name,
                                productId = productId,
                                userId = userId,
                                quantity = quantity,
                                totalPrice = totalPrice,
                                email = email,
                                address = address,
                                firstName = firstName,
                                lastName = lastName,
                                details_address = details_address,
                                city = city,
                                postalCode = postalCode,
                                transport = transport,
                                pay = pay,
                                statusBill = statusBill,
                                date = date,
                                orderId = orderId
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(orders)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("Orders")
            .document(orderId)
            .update("statusBill", newStatus)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun deleteOrder(orderId: String, onComplete: (Boolean) -> Unit) {
        firestore.collection("Orders").document(orderId)
            .delete()
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


/////////////////// User addres ///////////////////

    // Get all users - more efficient version with get() instead of real-time listener
    fun getAllUsers(onSuccess: (List<UserAddress>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("Users")
            .get()
            .addOnSuccessListener { result ->
                val usersList = result.documents.mapNotNull { doc ->
                    try {
                        val userId = doc.id
                        val user = doc.toObject(UserAddress::class.java)
                        user?.userId = userId
                        user
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(usersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getUserById(
        userId: String,
        onSuccess: (UserAddress?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("Users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserAddress::class.java)
                user?.userId = document.id
                onSuccess(user)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateUser(user: UserAddress, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = user.userId

        val userMap = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "address" to user.address,
            "city" to user.city,
            "state" to user.state,
            "pinCode" to user.pinCode,
            "country" to user.country,
            "phoneNumber" to user.phoneNumber
        )

        // Removed unnecessary cast to Map<String, Any>
        firestore.collection("Users").document(userId)
            .update(userMap as Map<String, Any>)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteUser(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("Users").document(userId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Get user orders
    fun getUserOrders(
        userId: String,
        onSuccess: (List<OrderDataModels>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("Orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ordersList = result.documents.mapNotNull { doc ->
                    val order = doc.toObject(OrderDataModels::class.java)
                    order?.orderId = doc.id
                    order
                }
                onSuccess(ordersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


////////////User Data /////////////////

    // Get all users - more efficient version with get() instead of real-time listener
    fun getAllUsersData(onSuccess: (List<UserData>) -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("UserData")
            .get()
            .addOnSuccessListener { result ->
                val usersList = result.documents.mapNotNull { doc ->
                    try {
                        val userId = doc.id
                        val user = doc.toObject(UserData::class.java)
                        user?.userId = userId
                        user
                    } catch (e: Exception) {
                        null
                    }
                }
                onSuccess(usersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getUserByIdData(
        userId: String,
        onSuccess: (UserData?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("UserData").document(userId)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(UserData::class.java)
                user?.userId = document.id
                onSuccess(user)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateUserData(user: UserData, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userId = user.userId

        val userMap = hashMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "address" to user.address,
            "email" to user.email,
            "password" to user.password,
            "phoneNumber" to user.phoneNumber,
            "profileImage" to user.profileImage,

            )

        // Removed unnecessary cast to Map<String, Any>
        firestore.collection("UserData").document(userId)
            .update(userMap as Map<String, Any>)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteUserData(userId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firestore.collection("UserData").document(userId)
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Get user orders
    fun getUserOrdersData(
        userId: String,
        onSuccess: (List<OrderDataModels>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("Orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val ordersList = result.documents.mapNotNull { doc ->
                    val order = doc.toObject(OrderDataModels::class.java)
                    order?.orderId = doc.id
                    order
                }
                onSuccess(ordersList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    /////////////////////// Order revenue /////////////////
    private val ordersCollection = firestore.collection("Orders")

    // Get all orders
    fun getOrdersRevenue(): Flow<List<OrderDataModels>> = callbackFlow {
        val listener = ordersCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { document ->
                    val order = document.toObject(OrderDataModels::class.java)
                    order?.orderId = document.id
                    order
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose { listener.remove() }
    }

    // Get delivered orders by date range
    fun getDeliveredOrdersByDateRange(startDate: String, endDate: String): Flow<List<OrderDataModels>> = callbackFlow {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val startTimestamp = dateFormat.parse(startDate)?.time ?: 0
        val endTimestamp = dateFormat.parse(endDate)?.time ?: System.currentTimeMillis()

        val listener = ordersCollection
            .whereEqualTo("statusBill", "Đã hoàn thành")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull { document ->
                    val order = document.toObject(OrderDataModels::class.java)
                    order?.orderId = document.id

                    // Filter by date
                    val orderDate = try {
                        dateFormat.parse(order?.date ?: "")?.time ?: 0
                    } catch (e: Exception) {
                        Log.e("FirebaseManager", "Lỗi parse date: ${order?.date}", e)
                        0
                    }

                    if (order != null && orderDate in startTimestamp..endTimestamp) {
                        order
                    } else {
                        null
                    }
                } ?: emptyList()

                trySend(orders)
            }

        awaitClose { listener.remove() }
    }

    // Get daily revenue data
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyRevenueData(startDate: String, endDate: String): Flow<Map<String, Double>> = callbackFlow {
        val startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val endLocalDate = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val listener = ordersCollection
            .whereEqualTo("statusBill", "Đã hoàn thành")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Group orders by date
                val dailyData = mutableMapOf<String, Double>()

                snapshot?.documents?.forEach { document ->
                    val order = document.toObject(OrderDataModels::class.java) ?: return@forEach

                    try {
                        val orderLocalDate = LocalDate.parse(order.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        if (!orderLocalDate.isBefore(startLocalDate) && !orderLocalDate.isAfter(endLocalDate)) {
                            dailyData[order.date] = dailyData.getOrDefault(order.date, 0.0) + order.totalPrice
                        }
                    } catch (e: Exception) {
                        // Skip invalid dates
                    }
                }

                trySend(dailyData)
            }

        awaitClose { listener.remove() }
    }

    // Get monthly revenue data
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyRevenueData(year: Int): Flow<Map<String, Double>> = callbackFlow {
        val listener = ordersCollection
            .whereEqualTo("statusBill", "Đã hoàn thành")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                // Group orders by month
                val monthlyData = mutableMapOf<String, Double>()

                snapshot?.documents?.forEach { document ->
                    val order = document.toObject(OrderDataModels::class.java) ?: return@forEach

                    try {
                        val orderDate = LocalDate.parse(order.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                        if (orderDate.year == year) {
                            val month = orderDate.monthValue
                            val monthName = "Tháng $month"
                            val monthKey = "$monthName/$year"

                            monthlyData[monthKey] = monthlyData.getOrDefault(monthKey, 0.0) + order.totalPrice
                        }
                    } catch (e: Exception) {
                        // Skip invalid dates
                    }
                }

                trySend(monthlyData)
            }

        awaitClose { listener.remove() }
    }
}



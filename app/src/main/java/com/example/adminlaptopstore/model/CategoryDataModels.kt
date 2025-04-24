package com.example.adminlaptopstore.model

data class CategoryDataModels(
    var id: String = "",  // ID tự động từ Firestore
    var name: String = "",
    var date: Long = System.currentTimeMillis(),
    var createBy:String = "",
    var categoryImage: String = "",

    ) {

}
package com.example.adminlaptopstore.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductDataModels(
    var productId: String = "",
    var name : String = "",
    var price : Double = 0.0,
    var short_desc: String = "",
    var long_desc: String = "",
    var quantity : Int = 0,
    var target : String = "",
    var category : String = "",
    var status : String = "",
    var image : String = "",
    var date : Long = System.currentTimeMillis(),
    var createBy : String = "",

    ){

}

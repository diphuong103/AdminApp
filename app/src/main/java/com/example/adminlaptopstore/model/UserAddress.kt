package com.example.adminlaptopstore.model

data class UserAddress(
    var userId: String = "",
    var firstName : String = "",
    var lastName : String = "",
    var address : String = "",
    var city : String = "",
    var state : String = "",
    var pinCode : String = "",
    var country : String = "",
    var phoneNumber : String = "",
)

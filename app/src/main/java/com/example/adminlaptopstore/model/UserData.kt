package com.example.adminlaptopstore.model

data class UserData(
    var userId: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    var address: String = "",
    var profileImage: String = "",
){
    fun toMap(): Map<String, Any> {

        var map = mutableMapOf<String, Any>()
        map["userId"] = userId
        map["firstName"] = firstName
        map["lastName"] = lastName
        map["email"] = email
        map["password"] = password
        map["phoneNumber"] = phoneNumber
        map["address"] = address
        map["profileImage"] = profileImage

        return map
    }
}

data class UserDataParent(
    var nodeId: String = "",
    var userData: UserData = UserData(),

    )
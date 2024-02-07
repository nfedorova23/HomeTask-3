package com.nfedorova.hometask3

data class Contact(
    val id: Int,
    var firstName: String?,
    var lastName: String?,
    var phoneNumber: String?,
    var isChecked: Boolean = false,
    var isDeleted: Boolean = false
)
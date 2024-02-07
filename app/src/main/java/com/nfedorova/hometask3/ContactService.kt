package com.nfedorova.hometask3

import kotlin.random.Random

class ContactService {

    fun getRandomContactList(): MutableList<Contact> =
        (0..100).map { getNewRandomContact() }.toMutableList()

    private fun getNewRandomContact(): Contact = Contact(
        id = Random.nextInt(),
        firstName = firstNameList.random(),
        lastName = lastNameList.random(),
        phoneNumber = getRandomPhoneNumber()
    )

    private fun getRandomPhoneNumber(): String =
        "8(${Random.nextInt(900, 999)})${Random.nextInt(100, 999)}-" +
                "${Random.nextInt(10, 99)}-${Random.nextInt(10, 99)}"

    companion object {
        private val firstNameList = listOf(
            "Alex",
            "Anthony",
            "Brandon",
            "Christopher",
            "David",
            "Ethan",
            "Fred",
            "Josh",
            "Kevin",
            "Tyler"
        )

        private val lastNameList = listOf(
            "Allen",
            "Clark",
            "Harris",
            "Jackson",
            "King",
            "Lewis ",
            "Miller",
            "Taylor",
            "Wilson",
            "Washington"
        )
    }
}
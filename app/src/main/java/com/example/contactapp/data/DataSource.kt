package com.example.contactapp.data

import android.content.Context
import com.example.contactapp.function.getContacts

class DataSource {
    companion object {
        private var INSTANCE: DataSource? = null
        fun getInstance(): DataSource {
            return INSTANCE ?: DataSource().apply { INSTANCE = this }
        }

    }

    var itemList = mutableListOf<ContactInformation>()

    var myContact = ContactInformation(
        name = "홍길동",
        phoneNumber = "010-5536-8898",
        email = "eastwest@flash.com",
        relationship = "본인"
    )
    fun getContactList(context: Context) {
        itemList = getContacts(context).toMutableList()
    }

    //todo 수정
    fun getDataList(): MutableList<ContactInformation> {
        return itemList
    }

    fun updateContact(position: Int, updatedContact: ContactInformation) {
        itemList[position] = updatedContact
    }

    fun addContact(contactInformation: ContactInformation) {
        itemList.add(contactInformation)
    }

    fun deleteContact(contactInformation: ContactInformation) {
        itemList.remove(contactInformation)
    }

    fun switchLike(position: Int) {
        itemList[position].isLike = !itemList[position].isLike
    }
}
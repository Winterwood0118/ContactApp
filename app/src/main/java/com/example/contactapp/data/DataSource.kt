package com.example.contactapp.data

import android.content.Context
import com.example.contactapp.function.getContacts

class DataSource {
    companion object{
        private var INSTANCE: DataSource? = null
        fun getInstance(): DataSource{
            return INSTANCE?: DataSource().apply { INSTANCE = this }
        }

    }
    var itemList = mutableListOf<ContactInformation>()

    fun getContactList(context: Context) {
        itemList = getContacts(context).toMutableList()
    }

    fun addContact(contactInformation: ContactInformation){
        itemList.add(contactInformation)
    }

    fun deleteContact(contactInformation: ContactInformation){
        itemList.remove(contactInformation)
    }

    fun switchLike(position: Int) {
        itemList[position].isLike = !contactList[position].isLike
    }
}
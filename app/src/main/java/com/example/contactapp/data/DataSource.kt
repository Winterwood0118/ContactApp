package com.example.contactapp.data

import android.content.Context
import com.example.contactapp.function.getContacts

class DataSource {
    companion object{
        private var INSTANCE: DataSource? = null
        fun getInstance(): DataSource{
            return synchronized(DataSource::class.java){
                val newInstance: DataSource = INSTANCE?: DataSource()
                INSTANCE = newInstance
                newInstance
            }
        }
    }


    fun getContactList(context: Context) = getContacts(context)

    fun addContact(contactInformation: ContactInformation){
        contactList.add(contactInformation)
    }

    fun deleteContact(contactInformation: ContactInformation){
        contactList.remove(contactInformation)
    }

    fun switchLike(position: Int) {
        contactList[position].isLike = !contactList[position].isLike
    }
}
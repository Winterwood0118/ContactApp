package com.example.contactapp.function

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.contactList
import com.example.contactapp.data.imageIdList

fun getContacts(context: Context): List<ContactInformation>{
    val contactsList = contactList
    val imageIdList = imageIdList

    for (i in contactsList.indices){
        contactList[i].imageRes = BitmapFactory.decodeResource(context.resources, imageIdList[i])
    }
    return contactsList
}


fun ImageView.switchHeart (isLike: Boolean){
    if (isLike){
        setImageResource(R.drawable.ic_heart)
    } else setImageResource(R.drawable.ic_heart_empty)
}
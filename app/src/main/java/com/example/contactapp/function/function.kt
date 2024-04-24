package com.example.contactapp.function

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.contactList
import com.example.contactapp.data.imageIdList
import com.example.contactapp.presentation.ContactListFragment
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.contactList
import com.example.contactapp.data.imageIdList
import java.io.IOException

fun getContacts(context: Context): List<ContactInformation> {
    val contactsList = contactList
    val imageIdList = imageIdList

    for (i in contactsList.indices) {
        contactList[i].imageRes = BitmapFactory.decodeResource(context.resources, imageIdList[i])
    }
    return contactsList
}

fun ImageView.switchHeart (isLike: Boolean){
    if (isLike){
        setImageResource(R.drawable.ic_heart)
    } else setImageResource(R.drawable.ic_heart_empty)
}

fun ImageView.setBitmapProfile(bitmap: Bitmap?){
    if(bitmap == null) setImageResource(R.drawable.ic_default_user)
    else setImageBitmap(bitmap)
}

//uri -> Bitmap 변환
fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


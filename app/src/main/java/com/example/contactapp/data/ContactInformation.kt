package com.example.contactapp.data

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactInformation(
    var name: String,
    var phoneNumber: String,
    var imageRes: Bitmap? = null,
    var isLike: Boolean = false,
    var relationship: String,
    var email: String
) : Parcelable



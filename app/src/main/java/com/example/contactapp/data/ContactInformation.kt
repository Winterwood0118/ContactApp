package com.example.contactapp.data

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactInformation(
    val name: String,
    val phoneNumber: Int,
    @DrawableRes
    val imageRes: Int,
    val isLike: Boolean = false,
    val relationship: String,
    val email: String
) : Parcelable

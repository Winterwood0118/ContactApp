package com.example.contactapp.function

import com.example.contactapp.data.ContactInformation

interface FragmentDataListener {
    fun onDataReeived(contact : ContactInformation)
}
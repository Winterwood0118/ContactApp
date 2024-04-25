package com.example.contactapp.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.DataSource
import com.example.contactapp.data.contactList
import com.example.contactapp.databinding.CustomDialogBinding

class AddContact : DialogFragment() {

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) //다이얼로그 최대치로 보여주기
    }


    private val binding: CustomDialogBinding by lazy {
        CustomDialogBinding.inflate(layoutInflater)
    }

    //다이얼로그 생성
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        val ivConfirm = binding.ivConfirm
        val ivReturn = binding.ivReturn
        val ivUser = binding.ivUser
        val ivDelete = binding.ivDelete
        val ivEdit = binding.ivEdit
        val editName = binding.editName
        val editEmail = binding.editEmail
        val editPhoneNumber = binding.editPhoneNumber
        val editRelationship = binding.editRelationship

        val getNameText = editName.text
        val getPhoneNumber = editPhoneNumber.text
        val getEmail = editEmail.text
        val getRelationship = editRelationship.text


            ivConfirm.setOnClickListener {
                val getDataSource = ContactInformation(
                    name = getNameText.toString(),
                    phoneNumber = getPhoneNumber.toString(),
                    email = getEmail.toString(),
                    relationship = getRelationship.toString(),
                    isLike = false,
                    imageRes = null
                )
                val dataSource = DataSource.getInstance()
                dataSource.addContact(getDataSource)
                dismiss()
                Log.d("데이터 확인","${DataSource().itemList}")
                Log.d("데이터 확인","${contactList}")
                Log.d("데이터확인","${getDataSource}")
                Log.d("데이터확인","${getNameText}")
                Log.d("데이터확인","${editName.text}")
            }


        return view
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

}
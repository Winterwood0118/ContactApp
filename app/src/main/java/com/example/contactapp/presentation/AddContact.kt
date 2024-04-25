package com.example.contactapp.presentation

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.contactapp.databinding.CustomDialogBinding
import com.example.contactapp.function.setBitmapProfile

class AddContact(private val position: Int) : DialogFragment() {
    interface OnDialogDismissListener{
        fun onDialogDismissed()
    }
    private var dismissListener: OnDialogDismissListener? = null
    fun setOnDialogDismissListener(listener: OnDialogDismissListener) {
        dismissListener = listener
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDialogDismissed()
    }
    private val dataSource = DataSource.getInstance()

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) //다이얼로그 최대치로 보여주기
    }


    private val binding: CustomDialogBinding by lazy {
        CustomDialogBinding.inflate(layoutInflater)
    }

    private val currentContact = when (position) {
        -1 -> dataSource.myContact
        in dataSource.itemList.indices -> dataSource.itemList[position]
        else -> null
    }

    //다이얼로그 생성
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (currentContact != null) {
            with(binding) {
                tvTitle.text = "EDIT"
                editName.setText(currentContact.name)
                editEmail.setText(currentContact.email)
                editPhoneNumber.setText(currentContact.phoneNumber)
                ivUser.setBitmapProfile(currentContact.imageRes)
                if (position == -1) {
                    tvRelationship.isVisible = false
                    editRelationship.isVisible = false
                } else {
                    editRelationship.setText(currentContact.relationship)
                }
                ivConfirm.setOnClickListener {
                    val resultContact = ContactInformation(
                        name = editName.text.toString(),
                        phoneNumber = editPhoneNumber.text.toString(),
                        email = editEmail.text.toString(),
                        imageRes = ivUser.drawable.toBitmap(),
                        relationship = editRelationship.text.toString()
                    )
                    if (position == -1) {
                        dataSource.myContact = resultContact
                    } else {
                        dataSource.itemList[position] = resultContact
                    }
                    dismiss()
                }

            }
        } else {
            with(binding) {
                ivConfirm.setOnClickListener {
                    val resultContact = ContactInformation(
                        name = editName.text.toString(),
                        phoneNumber = editPhoneNumber.text.toString(),
                        email = editEmail.text.toString(),
                        imageRes = ivUser.drawable.toBitmap(),
                        relationship = editRelationship.text.toString()
                    )
                    dataSource.addContact(resultContact)
                    dismiss()
                }
            }
        }

        binding.ivReturn.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

}
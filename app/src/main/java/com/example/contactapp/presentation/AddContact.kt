package com.example.contactapp.presentation

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.CustomDialogBinding
import com.example.contactapp.function.setBitmapProfile
import java.util.regex.Pattern

class AddContact(private val position: Int) : DialogFragment() {
    interface OnContactAddedListener {
        fun onContactAdded(contactInfo: ContactInformation)
    }
    
    private var contactAddedListener: OnContactAddedListener? = null
    fun setOnContactAddedListener(listener: OnContactAddedListener) {
        contactAddedListener = listener
    }
    
    interface OnDialogDismissListener {
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
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) //다이얼로그 크기 조정
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

                    contactAddedListener?.onContactAdded(resultContact)
                    dismiss()
                }

            }
        } else {
            with(binding) {
                var emailCheck: Boolean = false
                editEmail.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        if (emailPatternCheck(editEmail.text.toString())) {
                            emailCheck = true
                        } else {
                            emailCheck = false
                        }
                    }

                    override fun afterTextChanged(p0: Editable?) {
                    }
                })

                ivConfirm.setOnClickListener {
                    if (emailCheck){
                        val resultContact = ContactInformation(
                            name = editName.text.toString(),
                            phoneNumber = editPhoneNumber.text.toString(),
                            email = editEmail.text.toString(),
                            imageRes = ivUser.drawable.toBitmap(),
                            relationship = editRelationship.text.toString()
                        )
                        dataSource.addContact(resultContact)
                        contactAddedListener?.onContactAdded(resultContact)
                        dismiss()
                    }else{
                        Toast.makeText(requireActivity(),"이메일을 확인해 주세요",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.ivReturn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }
    override fun onDetach() {
        super.onDetach()
        contactAddedListener = null
    }

    //이메일 유효성 검사
    fun emailPatternCheck(id: String): Boolean {
        val idPattern =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$" // 이메일
        return (Pattern.matches(idPattern, id))
    }


    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

}
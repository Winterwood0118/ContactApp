package com.example.contactapp.presentation

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
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
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.CustomDialogBinding
import com.example.contactapp.function.setBitmapProfile

class AddContact(private val position: Int) : DialogFragment() {

    interface OnContactAddedListener {
        fun onContactAdded(contactInfo: ContactInformation)
    }
    private var contactAddedListener: OnContactAddedListener? = null
    fun setOnContactAddedListener(listener: OnContactAddedListener) {
        contactAddedListener = listener
    }

    //todo 시도 -위에 추가
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
        context?.dialogFragmentResize(this, 0.9f, 0.8f) //다이얼로그를 화면의 비율 90퍼로 나타내기
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
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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

                    contactAddedListener?.onContactAdded(resultContact) //todo 시도
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
                    contactAddedListener?.onContactAdded(resultContact) //todo 시도
                    dismiss()
                }
            }
        }

        binding.ivReturn.setOnClickListener {
            dismiss()
        }


        return binding.root
    }
    override fun onDetach() { //todo 시도
        super.onDetach()
        contactAddedListener = null
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }

    //화면 사이즈 구하기
    fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        if (Build.VERSION.SDK_INT < 30) {

            val display = windowManager.defaultDisplay //기기의 사이즈 정보
            val size = Point()

            display.getSize(size)

            val window = dialogFragment.dialog?.window //다이얼로그의 화면

            val x = (size.x * width).toInt()
            val y = (size.y * height).toInt()
            window?.setLayout(x, y)

        } else {

            val rect = windowManager.currentWindowMetrics.bounds

            val window = dialogFragment.dialog?.window

            val x = (rect.width() * width).toInt()
            val y = (rect.height() * height).toInt()

            window?.setLayout(x, y)
        }
    }
}
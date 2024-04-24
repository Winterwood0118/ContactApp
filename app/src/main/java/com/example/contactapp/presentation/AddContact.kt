package com.example.contactapp.presentation

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.contactapp.databinding.CustomDialogBinding

class AddContact : DialogFragment() {

    override fun onResume() {
        super.onResume()
        context?.dialogFragmentResize(this,0.9f,0.8f) //다이얼로그를 화면의 비율 90퍼로 나타내기
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

        var ivUser = binding.ivUser
        var ivDelete = binding.ivDelete
        var ivEdit = binding.ivEdit
        var editName = binding.editName
        var editEmail = binding.editEmail
        var editPhoneNumber = binding.editPhoneNumber
        var editRelationship = binding.editRelationship


        return view
    }

    companion object{
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
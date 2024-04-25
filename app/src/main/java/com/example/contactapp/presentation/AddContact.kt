package com.example.contactapp.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.contactapp.R
import com.example.contactapp.databinding.CustomDialogBinding
import java.io.IOException

class AddContact : DialogFragment() {
    private var selectedUri: Bitmap? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { //이미지를 선택할 경우
            //uri -> bitMap으로 변경
//            selectedUri = uriToBitmap(requireContext(), uri)
            selectedUri = (binding.ivUser.drawable as BitmapDrawable).bitmap //bitmap
            binding.ivUser.setImageBitmap(selectedUri)
        } else {
            Log.d("PhotoPicker", "No media selected") //todo 이미지 선택하지 않을 경우(알림추가?)
        }
    }
    //추가
    interface ContactDetailUpdateListener{
        fun onContactDetailUpdated(name:String,email:String,phoneNumber: String,relationShip:String,profile:Bitmap)
    }
    private lateinit var listener: ContactDetailUpdateListener
    private var updateName: String = ""
    private var updateEmail: String = ""
    private var updatePhoneNumber: String = ""
    private var updateRelationship: String = ""

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
        const val TAG = "ContactDetailUpdate" //tag
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

    fun setListener(update: ContactDetailUpdateListener){ //todo 이름 수정
        this.listener = update
    }

    //추가
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //완료 버튼
        binding.ivConfirm.setOnClickListener {
//                ivUser //todo 이미지 변경
            binding.apply {//todo 예외처리
                updateName = editName.text.toString()
                updateEmail = editEmail.text.toString()
                updatePhoneNumber = editPhoneNumber.text.toString()
                updateRelationship = editRelationship.text.toString()
/*                val imageBitmap = uriToBitmap(requireContext(), selectedUri)
                selectedUri = uriToBitmap(requireContext(),selectedUri)*/
                selectedUri = (ivUser.drawable as BitmapDrawable).bitmap

            }
            listener.onContactDetailUpdated(updateName,updateEmail,updatePhoneNumber,updateRelationship,selectedUri!!)
            dismiss()
        }
        //돌아가기 버튼
        binding.ivReturn.setOnClickListener {
            dismiss()
        }
        binding.ivEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.ivDelete.setOnClickListener {
            selectedUri = null
            binding.ivUser.setImageResource(R.drawable.ic_default_user)
        }
    }
    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
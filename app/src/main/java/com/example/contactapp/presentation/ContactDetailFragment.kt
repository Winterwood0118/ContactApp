package com.example.contactapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.contactapp.databinding.FragmentContactDetailBinding
import java.io.IOException

//uri -> bitMap으로 변경
class ContactDetailFragment : Fragment() {
    private var selectedUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { //이미지를 선택할 경우
            selectedUri = uri
            //uri -> bitMap으로 변경
            val imageBitmap = uriToBitmap(uri)
//            binding.ivProfile.setImageURI(uri) //uri로 가져오기
            binding.ivProfile.setImageBitmap(imageBitmap) //bitmap으로 가져오기
            Log.d("PhotoPicker", "$uri")
            Log.d("PhotoPicker", "$imageBitmap")
        } else {
            Log.d("PhotoPicker", "No media selected") //todo 이미지 선택하지 않을 경우(알림추가?)
        }
    }


    companion object {
        private const val REQUEST_CALL_PERMISSION = 1
    }


    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCall()
        setUpMessage()
        setUpProfile()
    }

    //전화
    private fun setUpCall() {
        binding.ivCall.setOnClickListener {
            binding.tvNumber.text = "01012345678" //임시 데이터
            val phoneNumber = binding.tvNumber.text
            val callIntent = Uri.parse("tel:$phoneNumber")
            // 권한 확인
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requireActivity().requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PERMISSION
                )
            } else {
                startActivity(Intent(Intent.ACTION_CALL, callIntent))
            }
        }
    }

    //문자 (전화와 동일)
    private fun setUpMessage() {
        binding.ivMessage.setOnClickListener {
            binding.tvNumber.text = "01012345678" //임시 데이터
            val phoneNumber = binding.tvNumber.text
            val sendUriSwipedPerson = Uri.parse("smsto:$phoneNumber")
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.SEND_SMS), REQUEST_CALL_PERMISSION)
            } else {
                startActivity(Intent(Intent.ACTION_SENDTO, sendUriSwipedPerson))
            }
        }
    }


    //photo picker 사용해서 이미지 선택
    private fun setUpProfile() {
        binding.ivAdd.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            //uri로부터 이미지에 대한 입력 스트림을 연다.
            BitmapFactory.decodeStream(inputStream)
            //입력 스트림에서 비트맵 디코딩
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
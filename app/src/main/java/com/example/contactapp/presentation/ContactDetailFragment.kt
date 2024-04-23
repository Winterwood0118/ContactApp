package com.example.contactapp.presentation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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

class ContactDetailFragment : Fragment() {
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            binding.ivProfile.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
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
            binding.tvNumber.text = "01012345678" //<<전화번호 값 입력
            val phoneNumber = binding.tvNumber.text
            val callUriSwipedPerson = Uri.parse("tel:$phoneNumber")
            // 권한 확인
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    REQUEST_CALL_PERMISSION
                ) //todo deprecated 수정
            } else {
                startActivity(Intent(Intent.ACTION_CALL, callUriSwipedPerson))
            }
        }
    }

    //문자 (전화와 동일)
    private fun setUpMessage() {
        binding.ivMessage.setOnClickListener {
            binding.tvNumber.text = "01012345678" //<<전화번호 값 입력
            val phoneNumber = binding.tvNumber.text
            val sendUriSwipedPerson = Uri.parse("smsto:$phoneNumber")
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.SEND_SMS), REQUEST_CALL_PERMISSION) //todo deprecated 수정
            } else {
                startActivity(Intent(Intent.ACTION_SENDTO, sendUriSwipedPerson))
            }
        }
    }


    //이미지
    private fun setUpProfile() {
        binding.ivAdd.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    //선택한 이미지 반영
/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            selectedImageUri?.let {
                // 선택된 이미지를 ImageView에 설정
                binding.ivProfile.setImageURI(selectedImageUri)
            }
        }
    }*/


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
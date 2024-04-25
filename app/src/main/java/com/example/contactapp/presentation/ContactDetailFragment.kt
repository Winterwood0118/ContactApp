package com.example.contactapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat.getParcelable
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.FragmentContactDetailBinding
import com.example.contactapp.function.uriToBitmap

class ContactDetailFragment : Fragment(), AddContact.ContactDetailUpdateListener {
    private var selectedUri: Uri? = null
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) { //이미지를 선택할 경우
            selectedUri = uri
            //uri -> bitMap으로 변경
            val imageBitmap = uriToBitmap(requireContext(), uri)
            binding.ivProfile.setImageBitmap(imageBitmap)
        } else {
            Log.d("PhotoPicker", "No media selected") //todo 이미지 선택하지 않을 경우(알림추가?)
        }
    }


    companion object {
        //요청 코드
        private const val REQUEST_CALL_PERMISSION = 1

        //방법 1
        /*        private const val SELECTED_DATA = "selectedData"

                fun newInstance(selectedData: ContactInformation): ContactDetailFragment {
                    val fragment = ContactDetailFragment()
                    val args = Bundle().apply {
                        putParcelable(SELECTED_DATA, selectedData)
                    }
                    fragment.arguments = args
                    return fragment
                }*/
    }


    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideTabLayout() //TabLayout 숨기기
        setUpCall()
        setUpMessage()
        setUpProfile()
        changeData()

        initData()

        binding.ivBack.setOnClickListener {
            goBack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }
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
                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
            } else {
                startActivity(Intent(Intent.ACTION_CALL, callIntent))
            }
        }
    }

    //문자
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
        binding.ivDelete.setOnClickListener {
            selectedUri = null
            binding.ivProfile.setImageResource(R.drawable.ic_default_user)
        }
    }

    //Detail 각 위젯에 데이터 띄우기 위함
    private fun inputEachData(data: ContactInformation) {
        binding.apply {
            ivProfile.setImageBitmap(data.imageRes)
            tvName.text = data.name
            tvNumber.text = data.phoneNumber
            tvEmail.text = data.email
            tvRelationship.text = data.relationship
        }
    }

    //데이터 삽입
    private fun initData() {
        if (Build.VERSION.SDK_INT >= 33) { // 33이상
            arguments?.getParcelable("selectedData", ContactInformation::class.java)?.let {
                inputEachData(it)
            }
        } else {// 33미만
            arguments?.getParcelable<ContactInformation>("selectedData")?.let { selectedData ->
                inputEachData(selectedData)
            }
        }
    }

    private fun goBack() {
        requireActivity().supportFragmentManager.popBackStack()
        (activity as? MainActivity)?.showTabLayout() //다시 TabLayout 보이기
        updateData() //Main으로 데이터 보내기
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onContactDetailUpdated(name: String, email: String, phoneNumber: String, relationShip: String) {
        binding.apply {
            tvName.text = name
            tvEmail.text = email
            tvNumber.text = phoneNumber
            tvRelationship.text = relationShip
        }
    }

    //data 수정
    private fun changeData() {
        val updateName = binding.tvName.text.toString()
        val updateEmail = binding.tvEmail.text.toString()
        val updatePhoneNumber = binding.tvNumber.text.toString()
        val updateRelationship = binding.tvRelationship.text.toString()
        binding.ivUpdate.setOnClickListener {


            val dialog = AddContact().apply {
                arguments = Bundle().apply {
                    putString("name", updateName)
                    putString("email", updateEmail)
                    putString("phoneNumber", updatePhoneNumber)
                    putString("relationship", updateRelationship)
                }
                setListener(this@ContactDetailFragment)
            }
            dialog.show(childFragmentManager, AddContact.TAG)
        }

    }

    //todo 변경될 값이 있을 경우에만 -> 추가
    private fun updateData() {
        val selectedPosition = arguments?.getInt("selectedPosition") //position 값
        val updateName = binding.tvName.text.toString()
        val updateEmail = binding.tvEmail.text.toString()
        val updatePhoneNumber = binding.tvNumber.text.toString()
        val updateRelationship = binding.tvRelationship.text.toString()

        if (selectedPosition != null) {
            val bundle = Bundle().apply {
                putInt("selectedPosition", selectedPosition)
                putString("updateName", updateName)
                putString("updateEmail", updateEmail)
                putString("updatePhoneNumber", updatePhoneNumber)
                putString("updateRelationship", updateRelationship)
            }
            parentFragmentManager.setFragmentResult("updateData", bundle)
        }
    }

}
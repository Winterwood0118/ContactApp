package com.example.contactapp.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.R
import com.example.contactapp.data.CallInformation
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.FragmentContactDetailBinding
import com.example.contactapp.function.setBitmapProfile
import com.example.contactapp.presentation.call_log.CallLogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContactDetailFragment : Fragment(), AddContact.ContactDetailUpdateListener {
    private lateinit var callLogAdapter: CallLogAdapter

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
//        setUpProfile()
        changeData()
        initData()

        binding.ivBack.setOnClickListener {
            goBack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goBack()
        }


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(emptyList()) // 초기화 시 빈 리스트 전달
        //이유? 데이터를 저장하는 것이 아닌, 해당 번호와 일치하는 전화기록을 해당 Fragment가 열리면 가져올 것이기 때문에 emptyList() 사용
        binding.recyclerView.adapter = callLogAdapter

        val phoneNumber = "01012345678"
        //DetailFragment에서 전화를 걸었을 때 실시간으로 RecyclerView가 변경되기 위한 코루틴 - CoroutineScope 생성
        lifecycleScope.launch {//lifecycle 사용하면 생명주기를 인식하는 코루틴 생성 가능
            while (true) {
                val callRecords = getCallLogByPhoneNumber(phoneNumber) // <<일치하는 번호 input하기
                callLogAdapter.callLog = callRecords
                callLogAdapter.notifyItemChanged(0)
            }
        }
    }

    //전화
    private fun setUpCall() {
        binding.ivCall.setOnClickListener {
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
/*    private fun setUpProfile() {
        binding.ivAdd.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.ivDelete.setOnClickListener {
            selectedUri = null
            binding.ivProfile.setImageResource(R.drawable.ic_default_user)
        }
    }*/

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

    override fun onContactDetailUpdated(name: String, email: String, phoneNumber: String, relationShip: String,profile:Bitmap) {
        binding.apply {
            tvName.text = name
            tvEmail.text = email
            tvNumber.text = phoneNumber
            tvRelationship.text = relationShip
            //profile

        }
    }

    //data 수정
    private fun changeData() {
        val updateName = binding.tvName.text.toString()
        val updateEmail = binding.tvEmail.text.toString()
        val updatePhoneNumber = binding.tvNumber.text.toString()
        val updateRelationship = binding.tvRelationship.text.toString()
        //이미지 가져오기
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

    //전화 기록 가져오기
    private suspend fun getCallLogByPhoneNumber(phoneNumber: String): List<CallInformation> {
        //suspend ? (코루틴을 사용하기 위해 사용)
        //코루틴 호출 시 일시 중단될 수 있다'를 알림
        val callRecords = mutableListOf<CallInformation>()

        //전화 기록에 전화번호와 일치하는 DB 가져오기
        withContext(Dispatchers.IO) { //백그라운드 작업 처리 위함
            //Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null); 참조
            //Cursor c = cr.query(Selection, Selection Arguments, Group By, Having, Order By); 참조
            val cursor = context?.contentResolver?.query( //sql query: 데이터 검색을 위해 사용한 메서드
                CallLog.Calls.CONTENT_URI,
                null,
                phoneNumber + "=?",
                arrayOf(phoneNumber),
                null
            )


            //전체 전화기록을 돌면서 해당하는 값을 가져오기
            cursor?.use {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

                while (it.moveToNext()) {//전체 전화기록을 돌면서 해당하는 번호의 데이터 callRecord에 추가
                    //휴대폰 번호
                    val phNumber = it.getString(numberIndex)

                    //전화 시간
//                    val callDate = Date(it.getLong(dateIndex)).toString() //GMT 그리니치 표기법 (출력: Wed Apr 24 03:05:22 GMT 2024)
                    val callDate = Date(it.getLong(dateIndex))
                    val simpleCallDate = SimpleDateFormat("MM월dd일 HH시mm분", Locale.getDefault())
                    val newCallDate = simpleCallDate.format(callDate)

                    //통화시간
                    val callDuration = Date(it.getLong(durationIndex))
                    val simpleCallDuration = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val newCallDuration = simpleCallDuration.format(callDuration)

                    //전화 타입
                    val callType = when (it.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "수신"
                        CallLog.Calls.OUTGOING_TYPE -> "발신"
                        CallLog.Calls.MISSED_TYPE -> "부재중"
                        else -> "" //todo 뭐로하지
                    }

                    val callRecord = CallInformation(phNumber,callType, newCallDate, newCallDuration)
                    callRecords.add(callRecord)
                }
            }
        }
        return callRecords //반환
    }


}
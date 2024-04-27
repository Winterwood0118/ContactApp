package com.example.contactapp.presentation

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.data.CallInformation
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.FragmentContactDetailBinding
import com.example.contactapp.presentation.call_log.CallLogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//todo 수정1)정보 수정했을 때만 update
//todo 수정2)전화 기록 update 문제
class ContactDetailFragment : Fragment(), AddContact.OnContactAddedListener {
    private lateinit var callLogAdapter: CallLogAdapter
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!


    companion object {
        private const val REQUEST_CALL_PERMISSION = 1 //요청 코드
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        isPermissionCallLog()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCall()
        setUpMessage()
        initData()
        changeData()

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
                    requireContext(), Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requireActivity().requestPermissions(arrayOf(Manifest.permission.SEND_SMS), REQUEST_CALL_PERMISSION)
            } else {
                startActivity(Intent(Intent.ACTION_SENDTO, sendUriSwipedPerson))
            }
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
        updateData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //데이터 수정 (Dialog 띄우기)
    private fun changeData() {
        binding.ivUpdate.setOnClickListener {
            val selectedPosition = arguments?.getInt("selectedPosition")

            val dialog = AddContact(selectedPosition ?: -1).apply {
                setOnContactAddedListener(this@ContactDetailFragment)
            }
            dialog.show(parentFragmentManager, AddContact.TAG)
        }
    }

    //main 정보 전달
    // todo 수정1
    private fun updateData() {
        val selectedPosition = arguments?.getInt("selectedPosition") //position 값
        val updateName = binding.tvName.text.toString()
        val updateEmail = binding.tvEmail.text.toString()
        val updatePhoneNumber = binding.tvNumber.text.toString()
        val updateRelationship = binding.tvRelationship.text.toString()

        // 기존 데이터 가져오기
        val oldName = arguments?.getString("updateName")
        val oldEmail = arguments?.getString("updateEmail")
        val oldPhoneNumber = arguments?.getString("updatePhoneNumber")
        val oldRelationship = arguments?.getString("updateRelationship")

        // 변경된 데이터와 기존 데이터를 비교하여 변경 여부 확인
        val dataChanged = updateName != oldName ||
                updateEmail != oldEmail ||
                updatePhoneNumber != oldPhoneNumber ||
                updateRelationship != oldRelationship

        if (selectedPosition != null && dataChanged) {
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

    override fun onContactAdded(contactInfo: ContactInformation) {
        inputEachData(contactInfo)
    }

    //전화 기록권한 설정
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.READ_CALL_LOG] == true && permissions[Manifest.permission.WRITE_CALL_LOG] == true) {

            initCallLogRecyclerView()
        } else {

            Toast.makeText(requireContext(), "권한을 거부하여서 전화기록을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isPermissionCallLog() {
        val shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.READ_CALL_LOG
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.WRITE_CALL_LOG
        )
        if (shouldShowRationale) {
            showPermissionDialog("전화 기록을 가져오기 위한 권한이 필요합니다.")
        } else {
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
            )
        }
    }
    //권한 요청 전 dialog
    private fun showPermissionDialog(text: String) {
        // Explain to the user why your app requires the permissions.
        AlertDialog.Builder(requireContext())
            .setMessage(text)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                requestPermissionLauncher.launch(
                    arrayOf(Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG)
                )
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    //recyclerView 초기 설정
    private fun initCallLogRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(emptyList()) // 초기화 시 빈 리스트 전달
        //이유? 데이터를 저장하는 것이 아닌, 해당 번호와 일치하는 전화기록을 해당 Fragment가 열리면 가져올 것이기 때문에 emptyList() 사용
        binding.recyclerView.adapter = callLogAdapter


        //todo 수정2
        //DetailFragment에서 전화를 걸었을 때 실시간으로 RecyclerView가 변경되기 위한 코루틴 - CoroutineScope 생성
        lifecycleScope.launch {//lifecycle 사용하면 생명주기를 인식하는 코루틴 생성 가능
            while (true) {
                // todo 알림 액션 시 ->
                // todo 랜딩

                val phoneNumber = binding.tvNumber.text.toString().replace("-", "")

                val callRecords = getCallLogByPhoneNumber(phoneNumber) // <<일치하는 번호 input하기
                if (callLogAdapter.callLog != callRecords) { // 데이터가 변경되었을 때만 업데이트
                    callLogAdapter.callLog = callRecords
                    callLogAdapter.notifyItemInserted(0)
                }
                delay(1000) // 1초마다 체크 todo
            }
        }
    }

    //전화 기록 가져오기
    private suspend fun getCallLogByPhoneNumber(phoneNumber: String): List<CallInformation> {
        //suspend ? (코루틴을 사용하기 위해 사용)
        //코루틴 호출 시 일시 중단될 수 있다'를 알림

        val callRecords = mutableListOf<CallInformation>()
        Log.d("callNumber", phoneNumber)

        //전화 기록에 전화번호와 일치하는 DB 가져오기
        withContext(Dispatchers.IO) { //백그라운드 작업 처리 위함
            //Cursor c = cr.query(CallLog.Calls.CONTENT_URI, null, null, null, null); 참조
            //Cursor c = cr.query(Selection(WHERE), Selection Arguments, Group By, Having, Order By);
            val cursor = context?.contentResolver?.query( //sql query: 데이터 검색을 위해 사용한 메서드
                CallLog.Calls.CONTENT_URI,
                null,
                "${CallLog.Calls.NUMBER}=?",
                arrayOf(phoneNumber),
                "${CallLog.Calls.DATE} DESC"
            )

            Log.d("cursor", "$cursor")
            //전체 전화기록을 돌면서 해당하는 값을 가져오기
            cursor?.use {
                val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

                while (it.moveToNext()) {//전체 전화기록을 돌면서 해당하는 번호의 데이터 callRecord에 추가
                    //휴대폰 번호
                    val phNumber = it.getString(numberIndex)
                    Log.d("phNumber", phNumber)

                    //전화 타입
                    val callType = when (it.getInt(typeIndex)) {
                        CallLog.Calls.INCOMING_TYPE -> "수신"
                        CallLog.Calls.OUTGOING_TYPE -> "발신"
                        CallLog.Calls.MISSED_TYPE -> "부재중"
                        else -> "거절"
                    }

                    //통화 날짜,시간
//                    val callDate = Date(it.getLong(dateIndex)).toString() //GMT 그리니치 표기법 (출력: Wed Apr 24 03:05:22 GMT 2024)
                    val callDate = Date(it.getLong(dateIndex))
                    val simpleCallDate = SimpleDateFormat("MM월dd일 HH시mm분", Locale.getDefault())
                    val newCallDate = simpleCallDate.format(callDate)

                    //통화시간
                    val callDuration = Date(it.getLong(durationIndex))
                    val simpleCallDuration = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val newCallDuration = simpleCallDuration.format(callDuration)

                    val callRecord = CallInformation(phNumber, callType, newCallDate, newCallDuration)
                    callRecords.add(callRecord)
                }
            }
        }
        return callRecords
    }
}
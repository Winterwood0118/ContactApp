package com.example.contactapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.data.CallInformation
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.FragmentContactDetailBinding
import com.example.contactapp.presentation.call_log.CallLogAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//todo 정보 수정했을 때만 update
class ContactDetailFragment : Fragment(),AddContact.OnContactAddedListener {
    private lateinit var callLogAdapter: CallLogAdapter
    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val REQUEST_CALL_PERMISSION = 1 //요청 코드
    }
    interface OnDetailDestroyedListener {
        fun onDetailDestroyed()
    }

    private var destroyedListener: OnDetailDestroyedListener? = null
    fun setOnDetailDestroyedListener(listener: OnDetailDestroyedListener) {
        destroyedListener = listener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.hideTabLayout() //TabLayout 숨기기
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


        //todo 전화 기록
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        callLogAdapter = CallLogAdapter(emptyList()) // 초기화 시 빈 리스트 전달
        //이유? 데이터를 저장하는 것이 아닌, 해당 번호와 일치하는 전화기록을 해당 Fragment가 열리면 가져올 것이기 때문에 emptyList() 사용
        binding.recyclerView.adapter = callLogAdapter

        val phoneNumber = binding.tvNumber.text.toString()
        //DetailFragment에서 전화를 걸었을 때 실시간으로 RecyclerView가 변경되기 위한 코루틴 - CoroutineScope 생성
/*        lifecycleScope.launch {//lifecycle 사용하면 생명주기를 인식하는 코루틴 생성 가능
            while (true) {
                val callRecords = getCallLogByPhoneNumber(phoneNumber) // <<일치하는 번호 input하기
                callLogAdapter.callLog = callRecords
                callLogAdapter.notifyItemChanged(0)
            }
        }*/
    }

    //전화
    private fun setUpCall() {
        binding.ivCall.setOnClickListener {
            val phoneNumber = binding.tvNumber.text
            val callIntent = Uri.parse("tel:$phoneNumber")
            // 권한 확인
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.CALL_PHONE
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
        (activity as? MainActivity)?.showTabLayout() //다시 TabLayout 보이기
        updateData() //Main으로 데이터 보내기
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeData() {
        binding.ivUpdate.setOnClickListener {
            val selectedPosition = arguments?.getInt("selectedPosition")

            val dialog = AddContact(selectedPosition ?: -1).apply {
                setOnContactAddedListener(this@ContactDetailFragment)
            }
            dialog.show(parentFragmentManager, AddContact.TAG)
        }
    }

    //todo 변경될 값이 있을 경우에만 update
    //main 정보 전달
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

    override fun onContactAdded(contact: ContactInformation) {
        inputEachData(contact)
    }

}
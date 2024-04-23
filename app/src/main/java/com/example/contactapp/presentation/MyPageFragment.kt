package com.example.contactapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.FragmentMyPageBinding
import com.example.contactapp.function.setBitmapProfile

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    private var param1: String? = null
    private var param2: String? = null

    private val myContact = ContactInformation(
        name = "홍길동",
        phoneNumber = "010-5536-8898",
        email = "eastwest@flash.com",
        relationship = "본인"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        with(binding) {
            etEmail.apply {
                setText(myContact.email)
                isEnabled = false
            }
            etName.apply {
                setText(myContact.name)
                isEnabled = false
            }
            etPhoneNumber.apply {
                setText(myContact.phoneNumber)
                isEnabled = false
            }
            ivProfile.setBitmapProfile(myContact.imageRes)
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
package com.example.contactapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.FragmentMyPageBinding
import com.example.contactapp.function.setBitmapProfile

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding

    private val myContact = ContactInformation(
        name = "홍길동",
        phoneNumber = "010-5536-8898",
        email = "eastwest@flash.com",
        relationship = "본인"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        fun newInstance() = MyPageFragment()
    }
}
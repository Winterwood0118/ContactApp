package com.example.contactapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.myContact
import com.example.contactapp.databinding.FragmentMyPageBinding
import com.example.contactapp.function.setBitmapProfile

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        with(binding) {
            tvEmail.text = myContact.email
            tvName.text = myContact.name
            tvPhoneNumber.text = myContact.phoneNumber
            ivProfile.setBitmapProfile(myContact.imageRes)
            ivAdd.setOnClickListener {
                AddContact(-1).show(requireActivity().supportFragmentManager, AddContact.TAG)
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = MyPageFragment()
    }
}
package com.example.contactapp.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.FragmentMyPageBinding
import com.example.contactapp.function.setBitmapProfile

class MyPageFragment : Fragment() {
    private lateinit var binding: FragmentMyPageBinding
    lateinit var dataSource: DataSource
    lateinit var myContact: ContactInformation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSource = DataSource.getInstance()
        myContact = dataSource.myContact
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(inflater, container, false)
        viewDataUpdate()
        setClickListener()
        return binding.root
    }

    private fun viewDataUpdate(){
        myContact = dataSource.myContact
        with(binding) {
            tvEmail.text = myContact.email
            tvName.text = myContact.name
            tvPhoneNumber.text = myContact.phoneNumber
            ivProfile.setBitmapProfile(myContact.imageRes)
        }
    }

    private fun setClickListener(){
        with(binding) {
            ivAdd.setOnClickListener {
                val addDialog = AddContact(-5)
                addDialog.setOnDialogDismissListener(object : AddContact.OnDialogDismissListener{
                    override fun onDialogDismissed() {
                        viewDataUpdate()
                    }
                })
                addDialog.show(parentFragmentManager, AddContact.TAG)
            }
            ivRewrite.setOnClickListener {
                val editDialog = AddContact(-1)
                editDialog.setOnDialogDismissListener(object : AddContact.OnDialogDismissListener{
                    override fun onDialogDismissed() {
                        viewDataUpdate()
                    }
                })
                editDialog.show(parentFragmentManager, AddContact.TAG)
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MyPageFragment()
    }
}
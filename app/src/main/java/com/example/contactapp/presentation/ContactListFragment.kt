package com.example.contactapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contactapp.R
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.FragmentContactListBinding
import com.example.contactapp.function.switchHeart


class ContactListFragment : Fragment() {
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contactAdapter = ContactListAdapter()
        val dataSource = DataSource.getInstance()
        dataSource.getContactList(requireActivity())
        contactAdapter.contactsList = dataSource.itemList
        binding.recyclerView.apply {
            adapter = contactAdapter
            layoutManager = LinearLayoutManager(context)
        }

        contactAdapter.itemClick = object : ContactListAdapter.ItemClick {
            override fun itemClick(view: View, position: Int) {
                val selectedData = dataSource.getDataList()[position]

                val detailFragment = ContactDetailFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable("selectedData", selectedData)
                        putInt("selectedPosition", position)
                    }
                }
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.frameLayout, detailFragment)
                    addToBackStack(null)
                    commit()
                    //binding.searchView.visibility = View.GONE //todo searchView 가리기 - 나타나기 구현
                }
            }
        }

        contactAdapter.heartClick = object : ContactListAdapter.HeartClick {
            override fun heartClick(view: View, position: Int) {
                dataSource.switchLike(position)
                (view as ImageView).switchHeart(contactAdapter.contactsList[position].isLike)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Detail 값 받아와서 적용
        parentFragmentManager.setFragmentResultListener("updateData", this) { _, bundle ->
            //
            val position = bundle.getInt("selectedPosition")
            val updateName = bundle.getString("updateName")
            val updateEmail = bundle.getString("updateEmail")
            val updatePhoneNumber = bundle.getString("updatePhoneNumber")
            val updateRelationship = bundle.getString("updateRelationship")

            updateSelectedDate(position, updateName!!, updateEmail!!, updatePhoneNumber!!, updateRelationship!!)
            Log.d("main","$position $updateName")
        }
    }

    //데이터 수정
    private fun updateSelectedDate(
        position: Int,
        updateName: String,
        updateEmail: String,
        updatePhoneNumber: String,
        updateRelationship: String
    ) {
        val dataList = DataSource.getInstance().getDataList()
        val updateData = dataList[position] //position 위치의 데이터 가져오기

        updateData.name = updateName
        updateData.email = updateEmail
        updateData.phoneNumber = updatePhoneNumber
        updateData.relationship = updateRelationship

        DataSource.getInstance().updateContact(position, updateData) //변경된 데이터를 데이터 소스에 다시 저장
        binding.recyclerView.adapter?.notifyItemChanged(position)
    }

    fun refreshView() {
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}
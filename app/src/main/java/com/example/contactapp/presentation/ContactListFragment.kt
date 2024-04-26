package com.example.contactapp.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.contactapp.R
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.FragmentContactListBinding
import com.example.contactapp.function.switchHeart


class ContactListFragment : Fragment() {
    private val binding by lazy { FragmentContactListBinding.inflate(layoutInflater) }

    private var listener: FragmentDataListener? = null

//    override fun onAttach(context : Context) {
//        super.onAttach(context)
//
//        if (context is FragmentDataListener) {
//            listener = context
//        } else {
//            throw RuntimeException("$context must implement FragmentDataListener")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.title = "3조 연락처 앱"
        toolbar.setTitleTextColor(resources.getColor(R.color.black))

    }


    lateinit var contactAdapter: ContactListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        contactAdapter = ContactListAdapter()
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
                    replace(R.id.clMain, detailFragment)
                    addToBackStack(null)
                    commit()
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_option -> {
                val popupMenu = PopupMenu(requireContext(), binding.toolbar.findViewById(R.id.action_option))
                popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
                popupMenu.show()
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.gridType -> {
                            // gridManager
                            binding.recyclerView.apply {
                                layoutManager = GridLayoutManager(context, 4)
                            }
                            return@setOnMenuItemClickListener true
                        }

                        R.id.listType -> {
                            // listManager
                            binding.recyclerView.layoutManager = LinearLayoutManager(context)
                            return@setOnMenuItemClickListener true
                        }

                        else -> {
                            return@setOnMenuItemClickListener false
                        }

    
        //Detail 값 받아와서 적용
        parentFragmentManager.setFragmentResultListener("updateData", this)
        { _, bundle ->
            //
            val position = bundle.getInt("selectedPosition")
            val updateName = bundle.getString("updateName")
            val updateEmail = bundle.getString("updateEmail")
            val updatePhoneNumber = bundle.getString("updatePhoneNumber")
            val updateRelationship = bundle.getString("updateRelationship")

            updateSelectedDate(
                position,
                updateName!!,
                updateEmail!!,
                updatePhoneNumber!!,
                updateRelationship!!
            )
            Log.d("main", "$position $updateName")
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

    //리사이클러뷰 갱신
    fun refreshView() {
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}
package com.example.contactapp.presentation

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
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

//        val toolbar = binding.toolbar
//        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
//        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
//        toolbar.title = "3조 연락처 앱"

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

        binding.ibOption.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(),it)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.show()
        }
    }

    // 작동 안함
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.toolbar_menu, menu)
//        // 메뉴 항목에 대한 추가 설정 및 처리
//    }


    // 액션버튼 메뉴 액션바에 집어 넣기
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.toolbar_menu,menu)
//
////        // Associate searchable configuration with the SearchView
////        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
////        (menu?.findItem(R.id.action_search)?.actionView as SearchView).apply {
////            setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
////        }
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when(item?.itemId) {
//            R.id.action_search -> {
//                Toast.makeText(requireContext(),"검색 이벤트 실행", Toast.LENGTH_SHORT).show()
//                return super.onOptionsItemSelected(item)
//            }
//            R.id.action_heart -> {
//                Toast.makeText(requireContext(),"즐겨찾기 이벤트 실행", Toast.LENGTH_SHORT).show()
//                return super.onOptionsItemSelected(item)
//            }
//            R.id.action_option -> {
//                // 기능 처리
//                return super.onOptionsItemSelected(item)
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

//    companion object {
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ContactListFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//
//        //Detail 값 받아와서 적용
//        parentFragmentManager.setFragmentResultListener("updateData", this) { _, bundle ->
//            //
//            val position = bundle.getInt("selectedPosition")
//            val updateName = bundle.getString("updateName")
//            val updateEmail = bundle.getString("updateEmail")
//            val updatePhoneNumber = bundle.getString("updatePhoneNumber")
//            val updateRelationship = bundle.getString("updateRelationship")
//
//            updateSelectedDate(position, updateName!!, updateEmail!!, updatePhoneNumber!!, updateRelationship!!)
//            Log.d("main","$position $updateName")
//        }
//    }

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
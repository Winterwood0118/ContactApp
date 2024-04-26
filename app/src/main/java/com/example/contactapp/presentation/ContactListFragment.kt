package com.example.contactapp.presentation

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.contactapp.R
import com.example.contactapp.data.DataSource
import com.example.contactapp.databinding.FragmentContactListBinding
import com.example.contactapp.function.FragmentDataListener
import com.example.contactapp.function.switchHeart

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Suppress("UNREACHABLE_CODE")
class ContactListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
                val detailData = contactAdapter.contactsList[position]
                listener?.onDataReeived(detailData)
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
                    }
                }
                true
            }

            R.id.action_search -> {
                true
            }

            R.id.action_heart -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ContactListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
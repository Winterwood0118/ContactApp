package com.example.contactapp.presentation

import android.opengl.Visibility
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

        /*        //방법1
                contactAdapter.itemClick = object : ContactListAdapter.ItemClick {
                    override fun itemClick(view: View, position: Int) {
                        val selectedData = dataSource.itemList[position]
                        val detailFragment = ContactDetailFragment.newInstance(selectedData)
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, detailFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }*/


        //방법2 -bundle
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
                    binding.searchView.visibility = View.GONE
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
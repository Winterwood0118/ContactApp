package com.example.contactapp.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.ListItemLayoutBinding

class ContactListAdapter()  : RecyclerView.Adapter<ContactListAdapter.Holder>(){

    var contacts = listOf<ContactInformation>()

    inner class Holder(private val binding : ListItemLayoutBinding, val onClick : (ContactInformation) -> Unit) : RecyclerView.ViewHolder(binding.root){
        private var currentItem: ContactInformation? = null

        init {
            itemView.setOnClickListener{
                currentItem?.let{
                    onClick(it)
                }
            }
        }

        fun bind(contact: ContactInformation){
            binding.apply {

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListAdapter.Holder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.Holder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

}
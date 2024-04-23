package com.example.contactapp.presentation

import android.media.Image
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.ListItemLayoutBinding
import com.example.contactapp.function.switchHeart

class ContactListAdapter(private val onClick: (ContactInformation) -> Unit)  : RecyclerView.Adapter<ContactListAdapter.Holder>(){

    var contacts = listOf<ContactInformation>()

    inner class Holder(private val binding : ListItemLayoutBinding, val onClick: (ContactInformation) -> Unit) : RecyclerView.ViewHolder(binding.root){
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
                ivProfile.setImageBitmap(contact.imageRes)
                tvName.text = contact.name
                tvNumber.text = contact.phoneNumber
            }
        }

        lateinit var ivHeart : ImageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListAdapter.Holder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.Holder, position: Int) {
        holder.bind(contacts[position])

        holder.ivHeart.switchHeart(contacts[position].isLike)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

}
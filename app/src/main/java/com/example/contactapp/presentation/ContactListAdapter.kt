package com.example.contactapp.presentation

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.data.ContactInformation
import com.example.contactapp.databinding.ListItemLayoutBinding
import com.example.contactapp.function.switchHeart

class ContactListAdapter()  : RecyclerView.Adapter<ContactListAdapter.Holder>(){

    var contacts = listOf<ContactInformation>()

    interface ItemClick {
        fun itemClick(view : View, position: Int)
    }

    interface HeartClick {
        fun heartClick(view: View, position: Int)
    }

    var itemClick : ItemClick? = null
    var heartClick : HeartClick? = null

    inner class Holder(private val binding : ListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(contact: ContactInformation){
            binding.apply {
                ivProfile.setImageBitmap(contact.imageRes)
                tvName.text = contact.name
                tvNumber.text = contact.phoneNumber
                ivHeart.switchHeart(contact.isLike)
            }
        }
        val ivHeart = binding.ivHeart
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactListAdapter.Holder {
        val binding = ListItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: ContactListAdapter.Holder, position: Int) {
        holder.apply {
            bind(contacts[position])
            itemView.setOnClickListener{
                itemClick?.itemClick(it, position)
            }
        }
        holder.ivHeart.setOnClickListener{
            heartClick?.heartClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

}
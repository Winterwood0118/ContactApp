package com.example.contactapp.presentation.call_log

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.data.CallInformation
import com.example.contactapp.databinding.CallLogItemBinding

class CallLogAdapter(var callLog: List<CallInformation>) :
    RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val binding = CallLogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLogList = callLog[position]
        holder.bind(callLogList)
    }

    override fun getItemCount(): Int {
        return callLog.size
    }

    class CallLogViewHolder(private val binding: CallLogItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(callInfo: CallInformation) {
            binding.apply {
                tvCallType.text = callInfo.callType
                tvCallDate.text = callInfo.callDate
                tvCallDuration.text = callInfo.callDuration

                when (callInfo.callType) {
                    "수신" -> tvCallType.setTextColor(ContextCompat.getColor(itemView.context, R.color.btn_green))
                    "발신" -> tvCallType.setTextColor(ContextCompat.getColor(itemView.context, R.color.btn_green))
                    "부재중" -> tvCallType.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                    else -> tvCallType.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
                }
            }
        }
    }
}
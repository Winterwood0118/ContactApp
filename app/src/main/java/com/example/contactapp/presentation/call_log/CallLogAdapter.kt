package com.example.contactapp.presentation.call_log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.R
import com.example.contactapp.data.CallInformation

class CallLogAdapter(private var callLog: List<CallInformation>) :
    RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallLogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_log_item, parent, false)
        return CallLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: CallLogViewHolder, position: Int) {
        val callLog = callLog[position]
        holder.bind(callLog)
    }

    override fun getItemCount(): Int {
        return callLog.size
    }

    class CallLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val callTypeTextView: TextView = itemView.findViewById(R.id.tvCallType)
        private val callDateTextView: TextView = itemView.findViewById(R.id.tvCallDate)
        private val callDurationTextView: TextView = itemView.findViewById(R.id.tvCallDuration)

        fun bind(callLog: CallInformation) {
            callTypeTextView.text = callLog.callType
            callDateTextView.text = callLog.callDate
            callDurationTextView.text = callLog.callDuration
        }
    }
}
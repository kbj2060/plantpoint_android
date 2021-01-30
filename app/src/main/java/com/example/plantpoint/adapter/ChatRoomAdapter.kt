package com.example.plantpoint.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.plantpoint.DTO.ChatRoom
import com.example.plantpoint.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat


class ChatRoomAdapter(options: FirestoreRecyclerOptions<ChatRoom?>) :
        FirestoreRecyclerAdapter<ChatRoom?, ChatRoomAdapter.ViewHolder?>(options) {

    @SuppressLint("SimpleDateFormat")
    var customFormat = SimpleDateFormat("yyyy-MM-dd");

    interface ItemClickListener {
        fun onClick(roomId: String, talkers: ArrayList<String>)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: Any) {
        this.itemClickListener = itemClickListener as ItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.ViewHolder {
        val view =
                LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_room_item, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                itemClickListener.onClick(this.roomId, this.talkers)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatRoom) {
        Glide.with(holder.itemView.context)
                .load(model.partnerProfileURL)
                .into(holder.partnerProfile)
        holder.partnerName.text = model.partnerName
        holder.lastMsg.text = model.lastMsg
        holder.timestamp.text = customFormat.format(model.timestamp!!)
        holder.roomId = model.roomId
        holder.talkers = model.talkers
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partnerProfile:ImageView = itemView.findViewById(R.id.partner_profile)
        val partnerName:TextView = itemView.findViewById(R.id.partner_name)
        val lastMsg:TextView = itemView.findViewById(R.id.last_msg)
        val timestamp: TextView = itemView.findViewById(R.id.last_date)
        var roomId = ""
        var talkers = arrayListOf<String>()
    }
}


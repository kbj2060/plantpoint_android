package com.plantpoint.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.plantpoint.dto.ChatRoom
import com.plantpoint.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.text.SimpleDateFormat


class ChatRoomAdapter(options: FirestoreRecyclerOptions<ChatRoom?>, me : Map<String, Any?>) :
        FirestoreRecyclerAdapter<ChatRoom?, ChatRoomAdapter.ViewHolder?>(options) {

    @SuppressLint("SimpleDateFormat")
    var customFormat = SimpleDateFormat("yyyy-MM-dd");
    var mMe = me;

    interface ItemClickListener {
        fun onClick(roomId: String, talkers: ArrayList<Map<String,String>>)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun setItemClickListener(itemClickListener: Any) {
        this.itemClickListener = itemClickListener as ItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.chat_room_item, parent, false)
        return ViewHolder(view).apply {
            itemView.setOnClickListener {
                itemClickListener.onClick(this.roomId, this.talkers)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ChatRoom) {
        holder.lastMsg.text = model.lastMsg
        holder.timestamp.text = customFormat.format(model.timestamp!!)
        holder.roomId = model.roomId
        holder.talkers = model.talkers
        model.talkers.remove(mMe)
        holder.partnerName.text = model.talkers[0]["name"]
        Glide.with(holder.itemView.context)
            .load(model.talkers[0]["profile"])
            .transform(CenterInside(), RoundedCorners(10))
            .into(holder.partnerProfile)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val partnerProfile:ImageView = itemView.findViewById(R.id.partner_profile)
        var partnerName:TextView = itemView.findViewById(R.id.partner_name)
        val lastMsg:TextView = itemView.findViewById(R.id.last_msg)
        val timestamp: TextView = itemView.findViewById(R.id.last_date)
        var roomId = ""
        var talkers = arrayListOf(mapOf<String, String>())
    }


}


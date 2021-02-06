package com.plantpoint.adapter

import Chat
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.plantpoint.R
import java.text.SimpleDateFormat

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ChatAdapter(options: FirestoreRecyclerOptions<Chat?>) :
    FirestoreRecyclerAdapter<Chat?, ChatAdapter.ViewHolder?>(options) {
    var user = FirebaseAuth.getInstance().currentUser
    @SuppressLint("SimpleDateFormat")
    var customFormat = SimpleDateFormat("yyyy-MM-dd");

    override fun onBindViewHolder(
			holder: ViewHolder,
			position: Int,
			model: Chat
    ) {
        holder.message.text = model.message
        holder.username.text = model.senderName
        holder.timestamp.text = customFormat.format(model.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View
        if (viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(parent.context).inflate(R.layout.chat_right, parent, false)
        else
            view = LayoutInflater.from(parent.context).inflate(R.layout.chat_left, parent, false)
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == user!!.uid) MSG_TYPE_RIGHT else MSG_TYPE_LEFT
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.sendername)
        var message: TextView = itemView.findViewById(R.id.textmessage)
        var timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    companion object {
        const val MSG_TYPE_LEFT = 0
        const val MSG_TYPE_RIGHT = 1
    }
}
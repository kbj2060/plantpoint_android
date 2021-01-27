package com.example.plantpoint.ui.chat

import Chat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.plantpoint.MainActivity
import com.example.plantpoint.adapter.ChatAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.plantpoint.R
import com.google.firebase.firestore.Query
import java.util.*

class ChatFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var textsend: EditText? = null
    var chatadapter: ChatAdapter? = null
    var db = FirebaseFirestore.getInstance()
    var chatref = db.collection("chat")
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(user == null){
            val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            navController.navigate(R.id.action_navigation_chat_to_navigation_require_login)
            return null
        }
        val root = inflater.inflate(R.layout.fragment_chat, container, false)

        val roomId = arguments?.getString("roomId").toString()
        val receiverUid = arguments?.getString("receiverUid").toString()
        val receiverName = arguments?.getString("receiverName").toString()
        val me = (activity as MainActivity).getMe()
        Log.d("dd", me["name"].toString())
        textsend = root.findViewById(R.id.edit_message)
        root.findViewById<View>(R.id.button_send).setOnClickListener {
            val chat = Chat(
                user.uid, me["name"].toString(), receiverUid, receiverName, textsend!!.text.toString(), Date(), roomId
            )
            chatref.add(chat)
            textsend!!.setText("")
        }

        recyclerView = root.findViewById(R.id.chat_recyclerview)
        linearLayoutManager = LinearLayoutManager(activity as MainActivity)
        linearLayoutManager!!.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
        val query = chatref.whereEqualTo("roomId", roomId)
                                    .orderBy("timestamp", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Chat?> =
            FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()
        chatadapter = ChatAdapter(options)
        chatadapter!!.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                recyclerView!!.scrollToPosition(chatadapter!!.itemCount - 1)
            }
        })
        recyclerView!!.adapter = chatadapter

        return root
    }

    override fun onStart() {
        super.onStart()
        chatadapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatadapter!!.stopListening()
    }
}
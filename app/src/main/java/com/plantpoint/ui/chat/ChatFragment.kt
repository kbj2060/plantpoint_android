package com.plantpoint.ui.chat

import Chat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.plantpoint.MainActivity
import com.plantpoint.adapter.ChatAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.plantpoint.R
import java.util.*

class ChatFragment : Fragment() {
    lateinit var root: View;
    var recyclerView: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var textsend: EditText? = null
    var chatadapter: ChatAdapter? = null
    var db = FirebaseFirestore.getInstance()
    var roomRef = db.collection("room")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            navController.navigate(R.id.action_navigation_chat_to_navigation_require_login)
            return null
        }

        root = inflater.inflate(R.layout.fragment_chat, container, false)
        val roomId = arguments?.getString("roomId").toString()
        val receiverUid = arguments?.getString("receiverUid").toString()
        val receiverName = arguments?.getString("receiverName").toString()
        val me = (activity as MainActivity).me
        val chatRef = roomRef.document(roomId).collection("chat")
        val chat = Chat(user.uid, me["name"].toString(), receiverUid, receiverName, "", Date(), roomId)

        sendText(roomId, chat);
        setChatRecyclerView(roomId);

        return root
    }

    override fun onStart() {
        super.onStart()
        chatadapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).closeKeyboard()
        chatadapter!!.stopListening()
    }

    private fun updateChatRoomLastMsg(roomId:String, msg:String){
        roomRef.document(roomId).update(mapOf("lastMsg" to msg))
    }

    private fun updateChat(roomId:String, chat:Chat){
        roomRef.document(roomId).collection("chat").add(chat)
    }

    private fun sendText(roomId:String, chat:Chat){
        val chatRef = roomRef.document(roomId).collection("chat")
        textsend = root.findViewById(R.id.edit_message)
        root.findViewById<View>(R.id.button_send).setOnClickListener {
            if(textsend!!.text.toString() == ""){
                return@setOnClickListener
            }
            chat.message = textsend!!.text.toString()
            updateChat(roomId, chat)
            updateChatRoomLastMsg(roomId, chat.message)
            textsend!!.setText("")
        }
    }

    private fun setChatRecyclerView(roomId:String) {
        val chatRef = roomRef.document(roomId).collection("chat")
        val query = chatRef.orderBy("timestamp", Query.Direction.ASCENDING)
        val options: FirestoreRecyclerOptions<Chat?> =
                FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat::class.java).build()

        recyclerView = root.findViewById(R.id.chat_recyclerview)
        linearLayoutManager = LinearLayoutManager(activity as MainActivity)
        linearLayoutManager!!.stackFromEnd = true
        recyclerView!!.layoutManager = linearLayoutManager
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
    }



}
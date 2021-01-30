package com.example.plantpoint.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantpoint.DTO.ChatRoom
import com.example.plantpoint.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.plantpoint.R
import com.example.plantpoint.adapter.ChatRoomAdapter
import com.google.firebase.firestore.Query

class ChattingRoomFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var chatRoomAdapter: ChatRoomAdapter? = null
    var db = FirebaseFirestore.getInstance()
    var roomRef = db.collection("room")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val directToChat = arguments?.getBoolean("directToChat")
        val roomId = arguments?.getString("roomId").toString()
        val receiverUid = arguments?.getString("receiverUid").toString()
        val receiverName = arguments?.getString("receiverName").toString()

        if (directToChat == true){
            val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            val bundle = bundleOf(
                "roomId" to roomId,
                "receiverUid" to receiverUid,
                "receiverName" to receiverName
            )
            navController.navigate(R.id.action_navigation_chat_room_to_navigation_chat, bundle)
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val navController =
                Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
            navController.navigate(R.id.action_navigation_chat_room_to_navigation_require_login)
            return null
        }

        val root = inflater.inflate(R.layout.fragment_chat_room, container, false)

        recyclerView = root.findViewById(R.id.chat_room_recyclerview)
        linearLayoutManager = LinearLayoutManager(activity as MainActivity)
        recyclerView!!.layoutManager = linearLayoutManager
        val query = roomRef
            .whereArrayContains("talkers", user.uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
        val options: FirestoreRecyclerOptions<ChatRoom?> =
            FirestoreRecyclerOptions.Builder<ChatRoom>().setQuery(
                query,
                ChatRoom::class.java
            ).build()
        chatRoomAdapter = ChatRoomAdapter(options)
        recyclerView!!.adapter = chatRoomAdapter
        (recyclerView!!.adapter as ChatRoomAdapter).setItemClickListener(object :
            ChatRoomAdapter.ItemClickListener {
            override fun onClick(roomId: String, talkers: ArrayList<String>) {
                val navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
                talkers.remove(user.uid)
                val fReceiverUid = talkers[0]
                val bundle = bundleOf(
                    "roomId" to roomId,
                    "receiverUid" to fReceiverUid
                )
                navController.navigate(R.id.action_navigation_chat_room_to_navigation_chat, bundle)
            }
        })

        return root
    }

    override fun onStart() {
        super.onStart()
        chatRoomAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatRoomAdapter?.stopListening()
    }

    fun navigateToChat() {

    }
}
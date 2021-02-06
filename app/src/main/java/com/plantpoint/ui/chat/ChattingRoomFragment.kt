package com.plantpoint.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plantpoint.dto.ChatRoom
import com.plantpoint.MainActivity
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.plantpoint.R
import com.plantpoint.adapter.ChatRoomAdapter
import com.google.firebase.firestore.Query

class ChattingRoomFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var chatRoomAdapter: ChatRoomAdapter? = null
    var db = FirebaseFirestore.getInstance()
    var roomRef = db.collection("room")

    private lateinit var navController : NavController
    private lateinit var root : View;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = Navigation.findNavController(activity as MainActivity, R.id.nav_host_fragment)
        root = inflater.inflate(R.layout.fragment_chat_room, container, false)

        /* user 선언 위치 바꾸지 말것. navigationUp 할 때마다 다시 받아와야 무한루프를 돌지 않음 */
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            navController.navigate(R.id.action_navigation_chat_room_to_navigation_require_login)
            return null
        }

        val args = requireArguments()
        val directToChat = args.getBoolean("directToChat")
        var me = (activity as MainActivity).me

        if (directToChat) {
            navigateToChat(args)
        }

        setChatRoomRecyclerView(me)

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

    private fun navigateToChat(args: Bundle){
        val bundle = bundleOf(
                "roomId" to args.getString("roomId").toString(),
                "receiverUid" to args.getString("receiverUid").toString(),
                "receiverName" to args.getString("receiverName").toString()
        )
        navController.navigate(R.id.action_navigation_chat_room_to_navigation_chat, bundle)
    }

    private fun getChatRoomOptions(me:Map<String, Any>):FirestoreRecyclerOptions<ChatRoom?>{
        val query = roomRef
                .whereArrayContains("talkers", mapOf("uid" to me["uid"], "name" to me["name"], "profile" to me["profile"]))
                .orderBy("timestamp", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<ChatRoom>().setQuery(query, ChatRoom::class.java).build()
    }

    private fun setChatRoomRecyclerView(me:Map<String, Any>) {
        val options = getChatRoomOptions(me)
        val mMe = mapOf("uid" to me["uid"], "name" to me["name"], "profile" to me["profile"])

        recyclerView = root.findViewById(R.id.chat_room_recyclerview)
        linearLayoutManager = LinearLayoutManager(activity as MainActivity)
        recyclerView!!.layoutManager = linearLayoutManager
        chatRoomAdapter = ChatRoomAdapter(options, mMe)
        recyclerView!!.adapter = chatRoomAdapter

        (recyclerView!!.adapter as ChatRoomAdapter).setItemClickListener(object :
                ChatRoomAdapter.ItemClickListener {
            override fun onClick(roomId: String, talkers: ArrayList<Map<String,String>>) {
                var receiver:Map<String, String> = mapOf();
                for(talker in talkers){
                    if(talker["uid"] !== me["uid"]){
                        receiver = talker
                    }
                }
                navigateToChat(
                        bundleOf(
                        "roomId" to roomId.trim(),
                        "receiverUid" to receiver["uid"],
                        "receiverName" to receiver["name"]
                        )
                );
            }
        })
    }
}
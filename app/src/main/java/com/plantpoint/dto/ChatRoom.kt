package com.plantpoint.dto

import java.util.*
import kotlin.collections.ArrayList

class ChatRoom(
        var roomId:String = "",
        var lastMsg:String = "",
        var validation: Map<String, Boolean> = mapOf<String, Boolean>(),
        var talkers: ArrayList<Map<String, String>> = arrayListOf(mapOf<String, String>()),
        var timestamp: Date? = Date()
)
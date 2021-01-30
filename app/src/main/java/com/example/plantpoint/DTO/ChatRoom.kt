package com.example.plantpoint.DTO

import java.util.*

class ChatRoom (
        var roomId :String = "",
        var hostUid:String = "",
        var partnerUid:String = "",
        var partnerName:String = "",
        var lastMsg:String = "",
        var partnerProfileURL:String = "",
        var talkers: ArrayList<String> = arrayListOf(),
        var timestamp: Date? = Date()
)
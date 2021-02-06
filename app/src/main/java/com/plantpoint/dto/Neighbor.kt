package com.example.plantpoint.dto

class Neighbor ( val id: String = "",
                 val profile: String = "",
                 val repArea: String = "",
                 val farmerName: String = "",
                 val farmerLocation:String = "",
                 val crops:Map<String, Int> = mapOf("" to 0),
                 val uid:String = ""
)
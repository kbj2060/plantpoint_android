package com.example.plantpoint.DTO

class Neighbor ( val id: String = "",
                 val profileURL: String = "",
                 val repArea: String = "",
                 val farmerName: String = "",
                 val farmerLocation:String = "",
                 val crops:Map<String, Int> = mapOf("" to 0),
                 val uid:String = ""
)
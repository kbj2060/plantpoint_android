package com.plantpoint.dto

class User (
    val type:String = "",
    val email:String = "",
    val address:String = "",
    val name:String = "",
    val registration: String = "",
    val profile:String = "",
    val rooms: ArrayList<String> = arrayListOf(),
    val stars: ArrayList<String> = arrayListOf(),
    val uid : String = ""
)
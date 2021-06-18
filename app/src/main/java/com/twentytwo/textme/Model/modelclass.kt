package com.twentytwo.textme.Model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Users(
    val uid: String = "",
    val proFileImageUrl: String = "",
    val name: String = "",
) : Serializable

data class UsersChats(
    val channelId:String="",val name:String=""
) : Serializable

data class UsersReg(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val password: String = "",
    val date_created: String = "",
    val gender: String = "",
    val proFileImageUrl: String = "",
) : Serializable

class Messaged(
    val messageText: String = "",
    val fromUid: String = "",
    @ServerTimestamp
    val sentAt: Date? = null
)

data class addContacts(
    val proFileImageUrl: String = "",
    val name: String = "",
    val uid: String = "",
)
///////////////////////////////////////////////////////////////////////


data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}


data class TextMessage(
    val text: String="",
    val time: Date? =null,
    val senderId: String="",
    val recipientId: String="",
    val senderName: String="",
)
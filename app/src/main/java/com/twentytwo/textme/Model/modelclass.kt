package com.twentytwo.textme.Model

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.*

data class Users(
    val uid: String = "",
    val proFileImageUrl: String = "",
    val name: String = "",
    val lastseen: String = "",
    val registrationTokens: MutableList<String>? = null,
) : Serializable

data class UsersChats(
    val channelId: String = "", val name: String = ""
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
    val registrationTokens: MutableList<String>? = null,

    ) : Serializable

class statustyping(
    val typing: String = "",
    val uid: String = "",
    @ServerTimestamp
    val sentAt: Date? = null
)

data class addContacts(
    val uid: String = "",
)
///////////////////////////////////////////////////////////////////////


data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}


//data class TextMessage(
//    override val imagePath: String,
//    override val text: String,
//    override val time: Date,
//    override val senderId: String,
//    override val recipientId: String,
//    override val senderName: String,
//    override val type: String = MessageType.TEXT
//) : Message {
//    constructor() : this("", Date(0), "", "", "")
//}

data class TextMessage(
    val imagePath: String,
    val text: String,
    override val time: Date,
    override val senderId: String,
    override val recipientId: String,
    override val senderName: String,
    override val type: String = "", override val seen: Int
) : Message {
    constructor() : this("", "", Date(0), "", "", "", "", 0)
}

data class Feeds(
    val uid:String="",
    val imagePath: String="",
    val location:String="",
    val title:String="",
    val descreption: String="",
    val uploadedTiem:Date?=null,
    val profileUrl: String=""
)
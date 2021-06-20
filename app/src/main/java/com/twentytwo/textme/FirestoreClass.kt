package com.twentytwo.textme

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.twentytwo.textme.ACTIVITIES_SEC.SignupActivity
import com.twentytwo.textme.Model.*
import java.text.SimpleDateFormat
import java.util.*

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()
    private val batch = mFireStore.batch()

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document(
            "UserSegment/${
                FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw NullPointerException("UID is null.")
            }"
        )

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    fun registeraUser(activity: SignupActivity, userInfo: UsersReg) {
        mFireStore.collection("USERDETAILS")
            .document(userInfo.uid)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                val user = Users(
                    userInfo.uid, userInfo.proFileImageUrl, userInfo.name, "",
                    mutableListOf()
                )
                mFireStore.collection("UserSegment")
                    .document(userInfo.uid)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        activity.userRegistrationSuccess(user.name)
                    }
                    .addOnFailureListener {
                        activity.userRegistrationFailure()
                    }
            }
            .addOnFailureListener {
                activity.userRegistrationFailure()
            }

    }

    fun getOrCreateChatChannel(
        otherUserId: String, onComplete: (channelId: String) -> Unit
    ) {
        currentUserDocRef.collection("engagedChatChannels")
            .document(otherUserId).get().addOnSuccessListener {
                if (it.exists()) {
                    onComplete(it["channelId"] as String)
                    return@addOnSuccessListener
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                val newChannel = chatChannelsCollectionRef.document()
                newChannel.set(ChatChannel(mutableListOf(currentUserId, otherUserId)))

                currentUserDocRef
                    .collection("engagedChatChannels")
                    .document(otherUserId)
                    .set(mapOf("channelId" to newChannel.id))

                firestoreInstance.collection("UserSegment").document(otherUserId)
                    .collection("engagedChatChannels")
                    .document(currentUserId)
                    .set(mapOf("channelId" to newChannel.id))

                onComplete(newChannel.id)
            }
    }

    fun sendMessage(message: TextMessage?, channelId: String) {
        if (message != null) {
            chatChannelsCollectionRef.document(channelId)
                .collection("messages")
                .add(message)
        }
    }

    //Image sceen by
    fun seen(touserid: String, channelId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        var messageCoolect = chatChannelsCollectionRef.document(channelId)
            .collection("messages")


        messageCoolect.whereEqualTo("senderId", touserid)
            .orderBy("time", Query.Direction.ASCENDING)
            .limit(50)
            .get().addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    for (d in t.result!!) {
                        messageCoolect.document(d.id).set(mapOf("read" to "true"))
                        Log.d("TAG", "seen: success")

                    }
                }
            }
    }


    ///fcm start
    fun getFCMRegistrationTokens(onComplete: (tokens: MutableList<String>?) -> Unit) {
        currentUserDocRef.get().addOnSuccessListener {
            val user = it.toObject(Users::class.java)!!
            onComplete(user.registrationTokens)
        }
    }

    fun setFCMRegistrationTokens(registrationTokens: MutableList<String>?) {
        currentUserDocRef.update(mapOf("registrationTokens" to registrationTokens))
    }
    //////fccccmmm

    fun addTyping(datas: statustyping, channelId: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        chatChannelsCollectionRef.document(channelId).collection("status").document(currentUserId)
            .set(datas)
    }

    fun getStatus(uid: String, currentChannelId: String, onComplete: (status: String) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        chatChannelsCollectionRef.document(currentChannelId).collection("status")
            .document(uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d("TAG", "Current data: ${snapshot.data?.get("typing")}")

                    val cities = ArrayList<Date>()
                    snapshot.getDate("sentAt")?.let {
                        //==========================================
                        Log.d("TAGS", "getStatus: $it")
                        val date1 = it
                        val sdf1 = SimpleDateFormat("HH:mm a")
                        val formatedDate1 = sdf1.format(date1)
                        var hS = formatedDate1.substring(0, 2).toInt()
                        val mS = formatedDate1.substring(3, 5).toInt()

                        //=============================================
                        val date = Calendar.getInstance().time
                        val sdf = SimpleDateFormat("HH:mm a")
                        val formatedDate = sdf.format(date)
                        var hN = formatedDate.substring(0, 2).toInt()
                        val mN = formatedDate.substring(3, 5).toIntOrNull() ?: 0

                        if (hN >= hS && mN >= mS) {
                            if (hN - hS <=1 && mN-mS <= 1) {

                                onComplete("ths uasd")
                            }
                        }
//                        onComplete(snapshot.data?.get("typing") as String)

                        //=========================================
//                        Log.d("TAG", "Current data: $hN:$mN$mN2  adn $hS:$mS$mS2")


                    }


                } else {
                    Log.d("TAG", "Current data: null")
                }

            }
    }
//


}
package com.twentytwo.textme

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.twentytwo.textme.ACTIVITIES_SEC.SignupActivity
import com.twentytwo.textme.Model.*

class FirestoreClass {
    private val mFireStore = FirebaseFirestore.getInstance()

    private val firestoreInstance: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val currentUserDocRef: DocumentReference
        get() = firestoreInstance.document("UserSegment/${FirebaseAuth.getInstance().currentUser?.uid
            ?: throw NullPointerException("UID is null.")}")

    private val chatChannelsCollectionRef = firestoreInstance.collection("chatChannels")

    fun registeraUser(activity: SignupActivity, userInfo: UsersReg) {
        mFireStore.collection("USERDETAILS")
            .document(userInfo.uid)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                val user = Users(userInfo.uid, userInfo.proFileImageUrl, userInfo.name)
                mFireStore.collection("UserSegment")
                    .document(userInfo.uid)
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        activity.userRegistrationSuccess(user.name)
                    }
                    .addOnFailureListener{
                        activity.userRegistrationFailure()
                    }
            }
            .addOnFailureListener {
                activity.userRegistrationFailure()
            }

    }

    fun getOrCreateChatChannel(otherUserId: String,
                               onComplete: (channelId: String) -> Unit) {
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
    fun sendMessage(message: TextMessage, channelId: String) {
        chatChannelsCollectionRef.document(channelId)
            .collection("messages")
            .add(message)
    }



}
package com.twentytwo.textme.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.twentytwo.textme.FirestoreClass

class MyFirebaseInstanceIDService : FirebaseMessagingService() {


     fun onTokenRefresh() {
        val newRegistrationToken = FirebaseMessaging.getInstance().token.toString()

        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(newRegistrationToken)
    }

    companion object {
        fun addTokenToFirestore(newRegistrationToken: String?) {
            if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

            FirestoreClass().getFCMRegistrationTokens { tokens ->
                if (tokens != null) {
                    if (tokens.contains(newRegistrationToken))
                        return@getFCMRegistrationTokens
                }

                tokens?.add(newRegistrationToken)
                FirestoreClass().setFCMRegistrationTokens(tokens)
            }
        }
    }
}
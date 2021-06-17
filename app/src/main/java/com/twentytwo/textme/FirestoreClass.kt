package com.twentytwo.textme

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.twentytwo.textme.ACTIVITIES_SEC.SignupActivity
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.Model.UsersReg

class FirestoreClass    {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registeraUser(activity: SignupActivity, userInfo: UsersReg) {
        mFireStore.collection("USERDETAILS")
            .document(userInfo.uid)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener {
                activity.userRegistrationFailure()
            }

    }


}
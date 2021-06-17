package com.twentytwo.textme.ui.Profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.CONTACTS.ADD_CONTACTS

data class UserList(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val password: String = "",
    val date_created: String = "",
    val gender: String = "",
    val termsncd: Boolean = false,
    val lkeystatus: Boolean = false,
    val lkey: String = ""

)

class Profile : Fragment() {


    private var filePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 1244

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        view.apply {

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val db = Firebase.firestore


            val docRef = db.collection("USERDETAILS").document("$uid")
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Document found in the offline cache
                    val document = task.result
                    if (document != null) {
                        val users = document.toObject<UserList>()
                        val tv_name = findViewById<TextView>(R.id.tv_name)
                        val tv_email = findViewById<TextView>(R.id.tv_email)
                        val tv_phone = findViewById<TextView>(R.id.tv_phone)
                        val tv_gender = findViewById<TextView>(R.id.tv_gender)
                        tv_name.setOnClickListener {
                            startActivity(Intent(context,ADD_CONTACTS::class.java))
                        }

                        tv_name.text = users?.name.toString()
                        tv_email.text = users?.email.toString()
                        tv_phone.text = users?.mobile.toString()
                        tv_gender.text = users?.gender

                    }
                }
            }
        ///////////////////////////

        }
        return view

    }


}
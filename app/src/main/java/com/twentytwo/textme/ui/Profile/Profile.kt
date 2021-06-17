package com.twentytwo.textme.ui.Profile

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
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R

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
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

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

                        tv_name.text = users?.name.toString()
                        tv_email.text = users?.email.toString()
                        tv_phone.text = users?.mobile.toString()
                        tv_gender.text = users?.gender

                    }
                }
            }
        }
        return view

    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment Profile.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic fun newInstance(param1: String, param2: String) =
//                Profile().apply {
//                    arguments = Bundle().apply {
//                        putString(ARG_PARAM1, param1)
//                        putString(ARG_PARAM2, param2)
//                    }
//                }
//    }
}
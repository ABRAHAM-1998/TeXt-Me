package com.twentytwo.textme.ui.CONTACTS

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.CHATS.ChatActivity
import com.twentytwo.textme.ui.home.MyListAdapter

class MyContacts : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_contacts, container, false)
        view.apply {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                val fromUid = firebaseUser.uid
                val rootRef = FirebaseFirestore.getInstance()
                val uidRef =
                    rootRef.collection("UserSegment").document(fromUid).collection("contacts")
                uidRef.get().addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        val listofids = ArrayList<String>()
                        for (d in t.result!!) {
                            listofids.add(d.id)
                        }
                        if (!listofids.isEmpty()){
                            val uidRefernce = rootRef.collection("UserSegment")
                                .whereIn("uid", listofids)
                            uidRefernce.get().addOnCompleteListener { t ->
                                if (t.isSuccessful) {
                                    val listUsers = ArrayList<Users>()
                                    for (d in t.result!!) {
                                        val toUser = d.toObject(Users::class.java)
                                        listUsers.add(toUser)
                                    }
                                    var list_viw = findViewById<ListView>(R.id.list_viw)
                                    list_viw.adapter = MyListAdapter(context, listUsers)
                                    list_viw.onItemClickListener =
                                        AdapterView.OnItemClickListener { _, _, position, _ ->
                                            val intent = Intent(context, ChatActivity::class.java)
                                            intent.putExtra("toUser", listUsers[position])
                                            startActivity(intent)
                                        }
                                }
                            }
                        }

                    }
                }
            }

            return view
        }

    }
}
package com.twentytwo.textme.ui.CONTACTS

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.CHATS.ChatActivity

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
                val uidRef = rootRef.collection("USERDETAILS").document(fromUid)
                uidRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            if (document.exists()) {
                                val fromUser = document.toObject(Users::class.java)
                                val userContactsRef =
                                    rootRef.collection("contacts").document(fromUid)
                                        .collection("userContacts")
                                userContactsRef.get().addOnCompleteListener { t ->
                                    if (t.isSuccessful) {
                                        val listOfToUserNames = ArrayList<String>()
                                        val listOfToUsers = ArrayList<Users>()
                                        val listOfRooms = ArrayList<String>()
                                        for (d in t.result!!) {
                                            val toUser = d.toObject(Users::class.java)
                                            listOfToUserNames.add(toUser.name)
                                            listOfToUsers.add(toUser)
                                            listOfRooms.add(d.id)
                                        }

                                        val arrayAdapter = ArrayAdapter(
                                            context,
                                            android.R.layout.simple_list_item_1,
                                            listOfToUserNames
                                        )

                                        val list_viw = findViewById<ListView>(R.id.list_viw)
                                        list_viw.adapter = arrayAdapter
                                        list_viw.onItemClickListener =
                                            AdapterView.OnItemClickListener { _, _, position, _ ->
                                                val intent =
                                                    Intent(activity, ChatActivity::class.java)
                                                intent.putExtra("fromUser", fromUser)
                                                intent.putExtra("toUser", listOfToUsers[position])
                                                intent.putExtra("roomId", "noRoomId")
                                                startActivity(intent)
                                            }
                                    }
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
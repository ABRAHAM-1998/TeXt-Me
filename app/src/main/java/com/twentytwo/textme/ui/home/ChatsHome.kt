package com.twentytwo.textme.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.twentytwo.textme.ACTIVITIES_SEC.ProfileActivity
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.Model.UsersChats
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.CHATS.ChatActivity


class ChatsHome : Fragment() {
    private var myRef: DatabaseReference? = null
    private var mFirebaseDatabase: FirebaseDatabase? = null

    private var firebaseAuth: FirebaseAuth? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_chats_home, container, false)
        view.apply {


            this@ChatsHome.authStateListener?.let { firebaseAuth!!.addAuthStateListener(it) }

            val firebaseUser = FirebaseAuth.getInstance().currentUser

            if (firebaseUser != null) {
                val fromUid = firebaseUser.uid
                val rootRef = FirebaseFirestore.getInstance()
                val uidRef = rootRef.collection("UserSegment").document(fromUid)
                    .collection("engagedChatChannels")
                uidRef.get().addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        val listids = ArrayList<String>()
                        val ChannelIds = ArrayList<String>()

                        for (d in t.result!!) {
                            listids.add(d.id)
                            var userQ = d.toObject(UsersChats::class.java)
                            ChannelIds.add(userQ.channelId)

                        }
                        if (listids.isNotEmpty()) {
                            val uidRefernce = rootRef.collection("UserSegment")
                                .whereIn("uid", listids)
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
                                            intent.putExtra("ChannelIds", ChannelIds[position])


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

    //enable options menu in this fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    //inflate the menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater!!.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //handle item clicks of menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //get item id to handle item clicks
        val id = item!!.itemId
        //handle item clicks
        if (id == R.id.add_friend) {
            //do your action here, im just showing toast
            startActivity(Intent(context, ProfileActivity::class.java))
        }
        if (id == R.id.sign_out_button) {
            //do your action here, im just showing toast
//            Toast.makeText(activity, "LOGOUIT", Toast.LENGTH_SHORT).show()
//            FirebaseAuth.getInstance().signOut()
//            startActivity(Intent(context, LoginActivity::class.java))
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            mFirebaseDatabase = FirebaseDatabase.getInstance()
            myRef = mFirebaseDatabase!!.getReference("hellow")
            // which is called with database reference.
            // which is called with database reference.
            myRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // inside the method of on Data change we are setting
                    // our object class to our database reference.
                    // data base reference will sends data to firebase.
                    val UsersChats = UsersChats("dfgdg", "fgfdgdg")
                    myRef!!.setValue(UsersChats)

                    // after adding this data we are showing toast message.
                    Toast.makeText(context, "data added", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    // if the data is not added or it is cancelled then
                    // we are displaying a failure toast message.
                    Toast.makeText(context, "Fail to add data $error", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
        return super.onOptionsItemSelected(item)
    }

}

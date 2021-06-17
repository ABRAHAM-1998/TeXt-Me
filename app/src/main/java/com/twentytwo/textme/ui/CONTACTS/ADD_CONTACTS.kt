package com.twentytwo.textme.ui.CONTACTS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twentytwo.textme.Model.UsersReg
import com.twentytwo.textme.Model.addContacts
import com.twentytwo.textme.R

class AddcontactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
class ADD_CONTACTS : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contacts)

        auth = FirebaseAuth.getInstance()

        val db = Firebase.firestore
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val query = db.collection("USERDETAILS")
            .orderBy("name")
        val options = FirestoreRecyclerOptions.Builder<UsersReg>()
            .setQuery(query, UsersReg::class.java)
            .setLifecycleOwner(this).build()
        val adapter = object : FirestoreRecyclerAdapter<UsersReg, AddcontactViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): AddcontactViewHolder {
                val view = LayoutInflater.from(this@ADD_CONTACTS)
                    .inflate(R.layout.item_add_contact_list, parent, false)
                return AddcontactViewHolder(view)
            }

            override fun onBindViewHolder(
                holder: AddcontactViewHolder,
                position: Int,
                model: UsersReg
            ) {
                var nickName = holder.itemView.findViewById<TextView>(R.id.nickNAme)
                var usName = holder.itemView.findViewById<TextView>(R.id.usrName)
                var usrLastseen = holder.itemView.findViewById<TextView>(R.id.usrLastSeen)
                var addFrnfBtn = holder.itemView.findViewById<Button>(R.id.addFrnfBtn)

                usName.text = model.name
                nickName.text = model.email
                addFrnfBtn.setOnClickListener {
                    funAddToContact(model.uid, model.name,model.proFileImageUrl)
                }

            }
        }
        val addUserRecyclerView = findViewById<RecyclerView>(R.id.addUserRecyclerView)
        addUserRecyclerView.adapter = adapter
        addUserRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun funAddToContact(uid: String, name: String, proFileImageUrl: String) {
        val db = Firebase.firestore
        val myuid = FirebaseAuth.getInstance().currentUser?.uid

        var data = addContacts(proFileImageUrl,name, uid)
        if (!uid.isEmpty()) {
            db.collection("contacts").document("$myuid")
                .collection("userContacts").document(uid)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this,"SUCCESS", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Faiilure", Toast.LENGTH_SHORT).show()
                }

        }

    }
}
package com.twentytwo.textme.ui.FEEDS

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twentytwo.textme.Model.Feeds
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.home.FeedsAdapter


class FragmentFeeds : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_feeds, container, false)
        view.apply {


            ///////////////////////
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val db = Firebase.firestore

            val query = db.collection("FEEDS")
//                .whereEqualTo("id", uid)
            query.get().addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    val FeedsItems = ArrayList<Feeds>()
                    for (d in t.result!!) {
                        val feeds = d.toObject(Feeds::class.java)
                        FeedsItems.add(feeds)
                    }
                    var list_viw = findViewById<ListView>(R.id.list_viw)
                    list_viw.adapter = FeedsAdapter(context, FeedsItems)

                }
            }


            }
            return view
        }
    }


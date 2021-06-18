package com.twentytwo.textme.ui.CHATS

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.twentytwo.textme.FirestoreClass
import com.twentytwo.textme.Model.TextMessage
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import java.util.*

class ChatActivity : AppCompatActivity() {
    private var rootRef: FirebaseFirestore? = null
    private var fromUid: String? = ""
    private var adapter: MessageAdapter? = null

    private lateinit var currentChannelId: String
    private lateinit var toUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        rootRef = FirebaseFirestore.getInstance()
        fromUid = FirebaseAuth.getInstance().currentUser!!.uid

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val toUser = intent.extras!!.get("toUser") as Users
        toUid = toUser.uid

//================================================================================================================
        FirestoreClass().getOrCreateChatChannel(toUid) { channelId ->
            currentChannelId = channelId
            //////////////////////////////////////////////////////////////////////////////////////////
            var button = findViewById<Button>(R.id.button)
            button.setOnClickListener {
                val edit_text = findViewById<EditText>(R.id.edit_text)
                val messageToSend =
                    FirebaseAuth.getInstance().currentUser?.displayName?.let { it1 ->
                        TextMessage(
                            edit_text.text.toString(), Calendar.getInstance().time,
                            FirebaseAuth.getInstance().currentUser!!.uid, toUid, it1
                        )
                    }
                edit_text.text.clear()
                if (messageToSend != null) {
                    FirestoreClass().sendMessage(messageToSend, channelId)
                }
            }
            ///////////////////////////////////////////////////////////////////////////////////
        }
//================================================================================================================================

        val handler = Handler()
        handler.postDelayed({

            if (!currentChannelId.isEmpty()) {
                // do something after 1000ms
                val query =
                    rootRef!!.collection("chatChannels").document(currentChannelId)
                        .collection("messages")
                        .orderBy("time", Query.Direction.ASCENDING)

                val options =
                    FirestoreRecyclerOptions.Builder<TextMessage>()
                        .setQuery(query, TextMessage::class.java)
                        .build()
                adapter = MessageAdapter(options)

                val recycler_view = findViewById<RecyclerView>(R.id.recycler_view)
                recycler_view.adapter = adapter

                recycler_view.layoutManager = LinearLayoutManager(this@ChatActivity)

                title = toUser.name
            }
        }, 500)
    }


    inner class MessageViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        internal fun setMessage(message: TextMessage) {
            val textView = view.findViewById<TextView>(R.id.text_view)

            val textTime = view.findViewById<TextView>(R.id.textTime)

            textView.text = message.text
            if (!message.time.toString().isEmpty()) {
                textTime.text = message.time.toString().take(16)
            }
        }
    }

    inner class MessageAdapter internal constructor(options: FirestoreRecyclerOptions<TextMessage>) :
        FirestoreRecyclerAdapter<TextMessage, MessageViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            return if (viewType == R.layout.item_message_to) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_to, parent, false)
                MessageViewHolder(view)
            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_from, parent, false)
                MessageViewHolder(view)
            }
        }

        override fun onBindViewHolder(
            holder: MessageViewHolder,
            position: Int,
            model: TextMessage
        ) {
            holder.setMessage(model)
        }

        override fun getItemViewType(position: Int): Int {
            return if (fromUid != getItem(position).senderId) {
                R.layout.item_message_to
            } else {
                R.layout.item_message_from
            }
        }

        override fun onDataChanged() {
            val recycler_view = findViewById<RecyclerView>(R.id.recycler_view)

            recycler_view.layoutManager?.scrollToPosition(itemCount - 1)
        }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(menuItem)
        }
    }


    override fun onStart() {
        super.onStart()
        val handler = Handler()
        handler.postDelayed({
            if (adapter != null) {
                adapter!!.startListening()
            }
        }, 500)
    }

    override fun onStop() {
        super.onStop()

        if (adapter != null) {
            adapter!!.stopListening()
        }
    }
}
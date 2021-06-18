package com.twentytwo.textme.ui.CHATS

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.twentytwo.textme.FirestoreClass
import com.twentytwo.textme.Model.TextMessage
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import com.twentytwo.textme.StorageUtil
import java.io.ByteArrayOutputStream
import java.util.*

private const val RC_SELECT_IMAGE = 2

class ChatActivity : AppCompatActivity() {
    private var rootRef: FirebaseFirestore? = null
    private var fromUid: String? = ""
    private var adapter: MessageAdapter? = null

    private lateinit var currentChannelId: String
    private lateinit var toUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatshome = findViewById<LinearLayout>(R.id.chatshome)
        chatshome.setBackgroundResource(R.drawable.wall2)


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
                            "",
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
        val fab_send_image = findViewById<ImageView>(R.id.fab_send_image)
        fab_send_image.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }
            startActivityForResult(Intent.createChooser(intent, "Select Image"), RC_SELECT_IMAGE)
        }
        //==========================================================================================================

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK &&
            data != null && data.data != null
        ) {
            val selectedImagePath = data.data

            val selectedImageBmp =
                MediaStore.Images.Media.getBitmap(contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()

            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
            val selectedImageBytes = outputStream.toByteArray()

            StorageUtil.uploadMessageImage(selectedImageBytes) { imagePath ->
                val messageToSend =
                    TextMessage(
                        imagePath, "", Calendar.getInstance().time,
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        toUid, "$fromUid",
                    )
                FirestoreClass().sendMessage(messageToSend, currentChannelId)
            }
        }
    }


    inner class MessageViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {
        internal fun setMessage(message: TextMessage) {



            if (message.imagePath.isNotEmpty()) {
                val imageView = view.findViewById<ImageView>(R.id.imageView)
                Glide.with(this@ChatActivity)
                    .load(message.imagePath)
                    .into(imageView)
            }
            if (!message.text.toString().isEmpty()) {
                val textView = view.findViewById<TextView>(R.id.text_view)
                textView.text = message.text
            }
            if (!message.text.toString().isEmpty()) {
                val textTime = view.findViewById<TextView>(R.id.textTime)
                textTime.text = message.time.toString().take(16)
            }
        }
    }

    inner class MessageAdapter internal constructor(options: FirestoreRecyclerOptions<TextMessage>) :
        FirestoreRecyclerAdapter<TextMessage, MessageViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            if (viewType == R.layout.item_message_to) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_to, parent, false)
                return MessageViewHolder(view)
            } else if (viewType == R.layout.item_message_from) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_from, parent, false)
                return MessageViewHolder(view)

            } else if (viewType == R.layout.item_message_from_image) {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_from_image, parent, false)
                return MessageViewHolder(view)

            } else {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_to_image, parent, false)
                return MessageViewHolder(view)
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
            if (fromUid != getItem(position).senderId && getItem(position).imagePath.isEmpty()) {
                return R.layout.item_message_to
            } else if (fromUid == getItem(position).senderId && getItem(position).imagePath.isEmpty()) {
                return R.layout.item_message_from
            } else if (fromUid == getItem(position).senderId && getItem(position).imagePath.isNotEmpty()) {
                return R.layout.item_message_from_image
            } else {
                return R.layout.item_message_to_image
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
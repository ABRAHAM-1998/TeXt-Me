package com.twentytwo.textme.webrtccall

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.twentytwo.textme.R

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Constants.isIntiatedNow = true
        Constants.isCallEnded = true
        val start_meeting = findViewById<Button>(R.id.start_meeting)
        start_meeting.setOnClickListener {
            val meeting_id = findViewById<EditText>(R.id.meeting_id)
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                db.collection("calls")
                    .document(meeting_id.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it["type"]=="OFFER" || it["type"]=="ANSWER" || it["type"]=="END_CALL") {
                            meeting_id.error = "Please enter new meeting ID"
                        } else {
                            Toast.makeText(this, "${it.id}", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@MainActivity, RTCActivity::class.java)
                            intent.putExtra("meetingID",meeting_id.text.toString())
                            intent.putExtra("isJoin",false)
                            startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        meeting_id.error = "Please enter new meeting ID"
                    }
            }
        }
        val join_meeting = findViewById<Button>(R.id.join_meeting)
        join_meeting.setOnClickListener {
            val meeting_id = findViewById<EditText>(R.id.meeting_id)
            if (meeting_id.text.toString().trim().isNullOrEmpty())
                meeting_id.error = "Please enter meeting id"
            else {
                val intent = Intent(this@MainActivity, RTCActivity::class.java)
                intent.putExtra("meetingID",meeting_id.text.toString())
                intent.putExtra("isJoin",true)
                startActivity(intent)
            }
        }
    }
}
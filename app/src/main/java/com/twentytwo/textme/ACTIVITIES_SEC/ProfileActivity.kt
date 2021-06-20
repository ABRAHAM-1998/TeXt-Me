package com.twentytwo.textme.ACTIVITIES_SEC

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import com.twentytwo.textme.ui.CONTACTS.ADD_CONTACTS
import com.twentytwo.textme.ui.Profile.UserList
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    private var NameUser: String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        storage = FirebaseStorage.getInstance()

        storageReference = storage!!.reference

        val chagepic = findViewById<ImageView>(R.id.ChangePicSettings)
        val UploadProfile = findViewById<Button>(R.id.UploadProfile)
        UploadProfile.visibility = View.INVISIBLE
        UploadProfile.setOnClickListener {
            fileUPload()
        }

        auth = FirebaseAuth.getInstance()

        val ImagePrewiew = findViewById<ImageView>(R.id.ImagePrewiew)
        var profilePicture = auth.currentUser?.photoUrl
        Glide.with(this)
            .load(profilePicture)
            .placeholder(R.drawable.logo)
            .into(ImagePrewiew)

        chagepic.setOnClickListener {
            UploadProfile.visibility = View.VISIBLE

            showFileChoser()
        }
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
                        startActivity(Intent(this, ADD_CONTACTS::class.java))
                    }

                    tv_name.text = users?.name.toString()
                    tv_email.text = users?.email.toString()
                    tv_phone.text = users?.mobile.toString()
                    tv_gender.text = users?.gender

                    if (users != null) {
                        NameUser = users.name
                    }


                }
            }
        }
        ///////////////////////////
    }

    private fun fileUPload() {


        if (filePath != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading")
            progressDialog.show()

            val imageRef =
                storageReference!!.child("ProfilePic/" + UUID.randomUUID().toString() + ".jpeg")
            imageRef.putFile(filePath!!)
                .addOnSuccessListener { taskSnapshot ->

                    imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                        Log.d("TAG", "onSuccess: uri= $uri")
                        //////////////////////////////////////////// >
                        val user = auth.currentUser
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri)
                            .build()

                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    val db = Firebase.firestore

                                    var data =
                                        Users(
                                            user.uid,
                                            uri.toString(),
                                            NameUser,
                                            "",
                                            mutableListOf()
                                        )
                                    val docRef = db.collection("UserSegment").document("$uid")
                                        .set(data, SetOptions.merge())
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                this,
                                                "SUCCESS",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Faiilure",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }


                                }
                            }


                        /////////////////////////////////////
                    }


                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "FilE UPLOADED SUCCESFULLY",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(applicationContext, "FAILED TO UOLOAD", Toast.LENGTH_SHORT)
                        .show()
                }
                .addOnProgressListener { tasKSnapshot ->
                    val progress =
                        100.0 * tasKSnapshot.bytesTransferred / tasKSnapshot.totalByteCount
                    progressDialog.setMessage("Uploaded   " + progress.toInt() + "   %...")
                }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            val result = CropImage.getActivityResult(data)
            if (result != null) {
                val ImagePrewiew = findViewById<ImageView>(R.id.ImagePrewiew)
                ImagePrewiew.setImageURI(result.uriContent)
            }
//            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, result?.uriContent)
            if (data != null) {
                if (result != null) {
                    filePath = result.uriContent
                }
            }

        }
    }

    private fun showFileChoser() {
        CropImage
            .activity()
            .setAspectRatio(1, 1)
            .setOutputCompressQuality(50)
            .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            .setBorderLineColor(Color.RED)
            .setActivityTitle("NOTE BOX CROPPER")
            .setCropMenuCropButtonTitle("Save Image")
            .start(this)


    }


}
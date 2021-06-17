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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.twentytwo.textme.Model.Users
import com.twentytwo.textme.R
import java.util.*


class ProfileActivity : AppCompatActivity() {

    private var filePath: Uri? = null
    internal var storage: FirebaseStorage? = null
    internal var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 1244
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

                    imageRef.getDownloadUrl().addOnSuccessListener(
                        OnSuccessListener<Uri> { uri ->
                            Log.d("TAG", "onSuccess: uri= $uri")
                            //////////////////////////////////////////// >
                            val user = auth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(user?.displayName)
                                .setPhotoUri(uri)
                                .build()

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        val db = Firebase.firestore

                                        var data = uid?.let { Users(it, uri.toString()) }
                                        val docRef = data?.let {
                                            db.collection("UserSegment").document("$uid")
                                                .set(it, SetOptions.merge())
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
                                }


                            /////////////////////////////////////
                        })


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
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                ImagePrewiew!!.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
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
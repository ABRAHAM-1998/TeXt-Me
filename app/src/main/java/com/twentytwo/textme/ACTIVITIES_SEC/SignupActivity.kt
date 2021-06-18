package com.twentytwo.textme.ACTIVITIES_SEC

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.twentytwo.textme.FirestoreClass
import com.twentytwo.textme.Model.UsersReg
import com.twentytwo.textme.R
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {

    //late iotems
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()

        //RADIIO BUTTON TASKS
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val registerButon = findViewById<Button>(R.id.registerButon)
        val sgn_email = findViewById<EditText>(R.id.sgn_email)
        val sgn_name = findViewById<EditText>(R.id.sgn_name)
        val sgn_phone = findViewById<EditText>(R.id.sgn_phone)
        val inputt_pass1 = findViewById<EditText>(R.id.inputt_pass1)
        val inputt_pass2 = findViewById<EditText>(R.id.inputt_pass2)
        val female_btn = findViewById<RadioButton>(R.id.female_btn)
        val male_btn = findViewById<RadioButton>(R.id.male_btn)
        val sign_llogTxt = findViewById<TextView>(R.id.sign_llogTxt)


        var gendertext = ""
        var CheckBox = false
        checkBox.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            CheckBox = b
        }

        radioGroup?.setOnCheckedChangeListener { group, checkedId ->
            gendertext = if (R.id.male_btn == checkedId) "male" else "female"
            Toast.makeText(applicationContext, gendertext, Toast.LENGTH_SHORT).show()
        }
        //=====================FULL-SCREEN===========================
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )
        }

        //=============================r====R=E=G=I=S=T=E=R=I=N=G==================
        registerButon.setOnClickListener {
            if (sgn_name.text.toString().isEmpty()) {
                sgn_name.error = "please enter name"
                sgn_name.requestFocus()
                return@setOnClickListener
            } else if (sgn_name.text.toString().length <= 3) {
                sgn_name.error = "please enter valid name"
                sgn_name.requestFocus()
                return@setOnClickListener
            }
            if (sgn_email.text.toString().isEmpty()) {
                sgn_email.error = "please enter email"
                sgn_email.requestFocus()
                return@setOnClickListener
            }
            if (sgn_phone.text.toString().isEmpty()) {
                sgn_phone.error = "please enter sgn_phone"
                sgn_phone.requestFocus()
                return@setOnClickListener
            } else if (sgn_phone.text.toString().length != 10) {
                sgn_phone.error = "Invalid Phone"
                sgn_phone.requestFocus()
                return@setOnClickListener
            }
            if (inputt_pass1.text.toString().isEmpty()) {
                inputt_pass1.error = "please enter inputt_pass1"
                inputt_pass1.requestFocus()
                return@setOnClickListener
            } else if (inputt_pass1.text.toString().length <= 5) {
                inputt_pass1.error = "please ente atleast 6 char"
                inputt_pass1.requestFocus()
                return@setOnClickListener
            }
            if (inputt_pass2.text.toString().isEmpty()) {
                inputt_pass2.error = "please enter inputt_pass2"
                inputt_pass1.requestFocus()
                return@setOnClickListener
            } else if (inputt_pass2.text.toString() != inputt_pass1.text.toString()) {
                inputt_pass2.error = "Passwords donot match!!"
                inputt_pass1.requestFocus()
                return@setOnClickListener
            }
            if (gendertext.isEmpty()) {
                female_btn.error = "select"
                male_btn.error = "select"
                radioGroup.requestFocus()
                return@setOnClickListener
            }
            if (!checkBox.isChecked) {
                checkBox.error = "please accept terms and conmditions"
                checkBox.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(
                sgn_email.text.toString(),
                inputt_pass2.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        //===================================
                        val sdf = SimpleDateFormat("dd/M/yyy hh:mm:ss")
                        val currentday = sdf.format(Date())


                        val fireebaseUser: FirebaseUser = task.result?.user!!
                        val userdetails = UsersReg(
                            fireebaseUser.uid,
                            sgn_name.text.toString().trim(),
                            sgn_email.text.toString().trim(),
                            sgn_phone.text.toString().trim(),
                            inputt_pass2.text.toString().trim(),
                            currentday.toString(),
                            gendertext,
                        )
                        FirestoreClass().registeraUser(this@SignupActivity, userdetails)
                        //-------------------
                        //============================/==============EMAIL-VERI=============================
                        user?.sendEmailVerification()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    sign_llogTxt.text =
                                        "AN EMAIL HASBEEN SEND TO YOUR EMAIL ADDRESS" +
                                                "\n PLESASE VERYFY TO LOGIN"
                                } else {
                                    sign_llogTxt.text = "UNABLE TO SEND EMAIL VERIFICATION"
                                }
                            }
                    } else {
                        sign_llogTxt.text =
                            "EMAIL ALREADY IN USE! OR UNABLE TO SEMD EMAIL ADDRESS" +
                                    "\n WRONG EMIAL, SIGN UP FAILED"
                    }
                }
            //////////////////////////////////////////////////////////////////////////////
        }
    }

    fun userRegistrationSuccess(name: String) {

        val user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user!!.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    Toast.makeText(this, "USER REGISTRATION SUCCESS", Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun userRegistrationFailure() {
        val sign_llogTxt = findViewById<TextView>(R.id.sign_llogTxt)
        sign_llogTxt.text = "USER REGISTRATION FAILED!"
        Toast.makeText(this, "USER REGISTRATION FAILED", Toast.LENGTH_SHORT).show()
    }
}
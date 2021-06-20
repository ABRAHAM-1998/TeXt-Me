package com.twentytwo.textme.ACTIVITIES_SEC

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.twentytwo.textme.R
import java.util.*

class SpalshScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh_screen)
        //---------------------------------------------------------------------------
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
        //--------------------------------------------------------------------
        //==========================TIMER===========================
        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@SpalshScreen, LoginActivity::class.java))
                finish()

            }
        }, 500L)
        //=--------------------------------------------------------------------
    }
}

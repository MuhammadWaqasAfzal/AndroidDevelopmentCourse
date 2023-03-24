package com.example.myapplication.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDelegate
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.common.getSpObject

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )
        Handler().postDelayed({

            if (getSpObject(this)?.getBoolean(Constants.LoggedIn,false) == true) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
//                window.clearFlags(
//                    WindowManager.LayoutParams.FLAG_FULLSCREEN
//                )

                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            }

        }, Constants.SPLASH_TIME)
    }


}
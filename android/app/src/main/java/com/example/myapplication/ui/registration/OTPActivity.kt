package com.example.myapplication.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import com.example.myapplication.R

class OTPActivity : AppCompatActivity() {
    var btnVerify: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btnVerify = findViewById(R.id.btnVerify);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle!!.text = getString(R.string.lbl_forgot_password)

        btnVerify!!.setOnClickListener {
            startActivity(Intent(this, NewPasswordActivity::class.java))
        }

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
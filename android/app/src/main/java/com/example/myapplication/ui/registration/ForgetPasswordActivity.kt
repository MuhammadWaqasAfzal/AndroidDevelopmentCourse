package com.example.myapplication.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R

class ForgetPasswordActivity : AppCompatActivity() {
    var btnSendOtp: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        btnSendOtp = findViewById(R.id.btnSendOtp);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle!!.text = getString(R.string.lbl_forgot_password)

        btnSendOtp!!.setOnClickListener {
            var intent = Intent(this, OTPActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
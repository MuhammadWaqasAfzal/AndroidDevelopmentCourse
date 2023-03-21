package com.example.myapplication.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class SignUpActivity : AppCompatActivity() {


    var btnSignup: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null
    var tvHaveAccount: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btnSignup = findViewById(R.id.btnSignup);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle!!.text = getString(R.string.lbl_forgot_password)

        btnSignup!!.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvHaveAccount!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}
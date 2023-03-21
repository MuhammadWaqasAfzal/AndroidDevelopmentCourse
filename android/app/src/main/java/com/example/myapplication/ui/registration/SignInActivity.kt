package com.example.myapplication.ui.registration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.myapplication.MainActivity
import com.example.myapplication.R

class SignInActivity : AppCompatActivity() {

    var btnSignUp: Button? = null
    var btnSignIn: Button? = null
    var tvForgotPassword: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        btnSignUp!!.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnSignIn!!.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
        tvForgotPassword!!.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
    }
}
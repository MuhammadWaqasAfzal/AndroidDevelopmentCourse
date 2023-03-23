package com.example.myapplication.ui.registration

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.R
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.isEmailValid
import com.example.myapplication.common.isInternetConnected
import com.example.myapplication.common.loader
import java.util.*

class ForgetPasswordActivity : AppCompatActivity() {
    var btnSendOtp: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null

    var edEmail: EditText? = null

    lateinit var activity: Activity
    lateinit var spinner: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activity = this;
        spinner = findViewById(R.id.spinner);


        edEmail = findViewById(R.id.edEmail);
        btnSendOtp = findViewById(R.id.btnSendOtp);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle!!.text = getString(R.string.lbl_forgot_password)

        btnSendOtp!!.setOnClickListener {
            if (isFieldsCorrect() && isInternetConnected(activity)) {
               // loader(activity, spinner, true);
                callApi()
            }


        }

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun callApi() {
        getSpObject(activity)?.edit()?.putString(Constants.Email,edEmail?.text.toString().trim().toLowerCase())?.commit();
        var intent = Intent(this, OTPActivity::class.java)
        startActivity(intent)
    }

    private fun isFieldsCorrect(): Boolean {
        when {
            edEmail?.text.toString() == "" -> {
                edEmail?.error = getString(R.string.email_required)
                edEmail?.requestFocus()
                return false
            }
            !isEmailValid(edEmail?.text.toString().toLowerCase(Locale.getDefault()).trim()) -> {
                edEmail?.error = getString(R.string.email_invalid)
                edEmail?.requestFocus()
                return false
            }
            else -> return true
        }
    }
}
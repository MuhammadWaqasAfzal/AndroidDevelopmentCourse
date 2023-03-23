package com.example.myapplication.ui.registration

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.login.GeneralResponse
import com.example.login.Login
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.common.*
import com.example.myapplication.models.ResetPassword
import com.example.myapplication.models.SignUpData
import com.example.myapplication.ui.api.MyApi
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class NewPasswordActivity : AppCompatActivity() {
    var btnChangePassword: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null
    var edPassword: EditText? = null
    var edConfirmPassword: EditText? = null
    lateinit var activity: Activity
    lateinit var spinner: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activity = this;
        spinner = findViewById(R.id.spinner);


        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);

        edPassword?.setText("1234567899")
        edConfirmPassword?.setText("1234567899");

        btnChangePassword = findViewById(R.id.btnChangePassword);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarTitle!!.text = getString(R.string.lbl_forgot_password)

        btnChangePassword!!.setOnClickListener {
            if (isFieldsCorrect() && isInternetConnected(activity)) {
                loader(activity, spinner, true);
                callApi()
            }

        }

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun isFieldsCorrect(): Boolean {
        when {
            edPassword?.text.toString() == "" -> {
                edPassword?.error = getString(R.string.password_required)
                edPassword?.requestFocus()
                return false
            }
            edConfirmPassword?.text.toString() == "" -> {
                edConfirmPassword?.error = getString(R.string.confirm_password_required)
                edConfirmPassword?.requestFocus()
                return false
            }
            edPassword?.text.toString() != edConfirmPassword?.text.toString() -> {
                edConfirmPassword?.error = getString(R.string.password_donot_match)
                edConfirmPassword?.requestFocus()
                edPassword?.error = getString(R.string.password_donot_match)
                edPassword?.requestFocus()
                return false
            }
            else -> return true
        }
    }

    private fun callApi() {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(16, TimeUnit.SECONDS)
            .readTimeout(16, TimeUnit.SECONDS)
            .writeTimeout(16, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://localhost:3000/php/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(MyApi::class.java);
        var data = ResetPassword(
            getSpObject(activity)?.getString(Constants.Email, "email").toString(),
            edPassword?.text.toString(),
            edConfirmPassword?.text.toString()
        );
        val call = api.resetPassword(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                loader(activity, spinner, false);
                if (response.code() == 200) {
                    var intent = Intent(activity, SignInActivity::class.java)
                    startActivity(intent)
                    finishAffinity()

                } else
                    showSnackBar(activity, response.message());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                loader(activity, spinner, false);
                Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG).show();

            }
        })
    }
}
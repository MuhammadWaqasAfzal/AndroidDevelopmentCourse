package com.example.myapplication.ui.registration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.login.Login
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.common.*
import com.example.myapplication.ui.api.MyApi
import com.example.myapplication.models.LoginData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit


class SignInActivity : AppCompatActivity() {

    var btnSignUp: Button? = null
    var btnSignIn: Button? = null
    var tvForgotPassword: TextView? = null
    var edEmail: EditText? = null
    var edPassword: EditText? = null
    lateinit var activity: Activity
    lateinit var spinner: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activity = this;
        spinner = findViewById(R.id.spinner);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        btnSignUp!!.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnSignIn!!.setOnClickListener {
            edEmail?.setText("waqasafzal1313@gmail.com");
            edPassword?.setText("12345678")
            if(isFieldsCorrect() && isInternetConnected(activity)){
                loader(activity,spinner, true);
                callApi()
            }
        }
        tvForgotPassword!!.setOnClickListener {
            startActivity(Intent(this, ForgetPasswordActivity::class.java))
        }
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
            edPassword?.text.toString() == "" -> {
                edPassword?.error = getString(R.string.password_required)
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
        var loginData = LoginData(edEmail?.text.toString().trim().toLowerCase(),edPassword?.text.toString());
        val call = api.login(loginData)
        call?.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                loader(activity,spinner, false);
                if(response.code()==200) {
                    val res = response.body()
                    var sp = getSpObject(this@SignInActivity)?.edit()
                    sp?.putString(Constants.Email, response.body()?.data?.Email)
                    sp?.putString(Constants.Id, response.body()?.data?.Id)
                    sp?.putString(Constants.FirstName, response.body()?.data?.FirstName)
                    sp?.putString(Constants.LastName, response.body()?.data?.LastName)
                    sp?.putString(Constants.Password, response.body()?.data?.Password)
                    sp?.putString(Constants.Gender, response.body()?.data?.Gender)
                    sp?.putBoolean(Constants.LoggedIn, true)
                    sp?.commit()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
                else
                    showSnackBar(activity,response.body()?.message.toString());
                   // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loader(activity,spinner, false);
                showSnackBar(activity, activity.getString(R.string.error_general));

            }
        })
    }
}
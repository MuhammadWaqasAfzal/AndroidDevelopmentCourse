package com.example.myapplication.ui.registration

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.login.Login
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.common.*
import com.example.myapplication.api.MyApi
import com.example.myapplication.models.SignUpData
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

class SignUpActivity : AppCompatActivity() {


    var btnSignup: Button? = null
    var btnMale: Button? = null
    var btnFemale: Button? = null
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null
    var tvHaveAccount: TextView? = null
    var edFirstName: EditText? = null
    var edLastName: EditText? = null
    var edEmail: EditText? = null
    var edPassword: EditText? = null
    var edConfirmPassword: EditText? = null
    lateinit var activity: Activity
    lateinit var spinner: ConstraintLayout
    var gender: Int = 0
    var isAdmin: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activity = this;
        spinner = findViewById(R.id.spinner);


        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);

        btnSignup = findViewById(R.id.btnSignup);
        tvHaveAccount = findViewById(R.id.tvHaveAccount);

        edFirstName = findViewById(R.id.edFirstName);
        edLastName = findViewById(R.id.edLastName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);

        edEmail?.setText("F1266urqan1212@gmail.com");
        edPassword?.setText("12345678")
        edFirstName?.setText("furqann");
        edLastName?.setText("khan")
        edConfirmPassword?.setText("12345678");

        //   ivBack = findViewById(R.id.ivBack);
        //  ivBack!!.visibility = View.VISIBLE

        // toolbarTitle = findViewById(R.id.toolbarTitle);
        //  toolbarTitle!!.text = getString(R.string.sign_up)

        btnSignup!!.setOnClickListener {

            if (isFieldsCorrect() && isInternetConnected(activity)) {
                loader(activity, spinner, true);
                callApi()
            }
        }

//        ivBack!!.setOnClickListener {
//            onBackPressedDispatcher.onBackPressed()
//        }

        tvHaveAccount!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnMale!!.setOnClickListener {
            gender = 0
            btnMale!!.background = getDrawable(R.drawable.bg_btn_yellow_left_half_round)
            btnMale!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_male_white), null, null, null);

            btnFemale!!.background = getDrawable(R.drawable.bg_btn_white_right_half_round)
            btnFemale!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_female_yellow), null, null, null);

        }


        btnFemale!!.setOnClickListener {
            gender = 1
            btnMale!!.background = getDrawable(R.drawable.bg_btn_white_right_half_round)
            btnMale!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_male_yellow), null, null, null);

            btnFemale!!.background = getDrawable(R.drawable.bg_btn_yellow_left_half_round)
            btnFemale!!.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_female_white), null, null, null);
        }
    }


    private fun isFieldsCorrect(): Boolean {
        when {
            edFirstName?.text.toString() == "" -> {
                edFirstName?.error = getString(R.string.first_name_required)
                edFirstName?.requestFocus()
                return false
            }
            edLastName?.text.toString() == "" -> {
                edLastName?.error = getString(R.string.last_name_required)
                edLastName?.requestFocus()
                return false
            }
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
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(MyApi::class.java);
        var data = SignUpData(
            edEmail?.text.toString().toLowerCase().trim(),
            edPassword?.text.toString(),
            edFirstName?.text.toString().trim(),
            edLastName?.text.toString().trim(),
            edConfirmPassword?.text.toString(),
            gender, isAdmin
        );
        val call = api.signUp(data)
        call?.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                loader(activity, spinner, false);
                if (response.code() == 201) {
                    val res = response.body()
                    var sp = getSpObject(activity)?.edit()
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
                } else
                    showSnackBar(activity, response.message());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loader(activity, spinner, false);
                Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG).show();

            }
        })
    }
}
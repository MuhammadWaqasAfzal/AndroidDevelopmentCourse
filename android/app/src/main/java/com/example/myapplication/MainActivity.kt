package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.alphaSquared.wifapp.common.Constants
import com.alphaSquared.wifapp.common.Constants.Companion.reviewFragment
import com.example.login.GeneralResponse
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.models.CreateReview
import com.example.myapplication.api.MyApi
import com.example.myapplication.ui.review.ReviewFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var activity: Activity

    // lateinit var spinner: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        activity = this;

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fabNewReview.setOnClickListener { view ->
            showAddReviewBottomDialog()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_message, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Constants.fab = binding.appBarMain.fabNewReview;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main2)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showAddReviewBottomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.create_review_bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetDialog.setContentView(dialogView)

        var btSubmit = bottomSheetDialog.findViewById<Button>(R.id.btSubmit);
        var edDescription = bottomSheetDialog.findViewById<EditText>(R.id.edDescription);
        var spinner = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.spinner);

        btSubmit?.setOnClickListener {
            if (!edDescription?.text.isNullOrBlank()) {
                spinner?.visibility = View.VISIBLE
                bottomSheetDialog.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );

                createReviewApi(bottomSheetDialog, edDescription?.text.toString(), spinner)
            } else
                showSnackBar(activity, getString(R.string.lbl_description))

        }


        bottomSheetDialog.show()
    }

    private fun createReviewApi(
        bottomSheetDialog: BottomSheetDialog,
        description: String,
        spinner: ConstraintLayout?
    ) {
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
        var data = CreateReview(
            getSpObject(activity)!!.getString(Constants.Email, "").toString(), description

        );
        val call = api.createReview(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                loader(activity, spinner!!, false);
                bottomSheetDialog.dismiss()
                showSnackBar(activity, response.message());
                if(response.code()==201)
                {
                    reviewFragment.callApiToGetReviews()
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                loader(activity, spinner!!, false);
                showSnackBar(activity, activity.getString(R.string.error_general));
                bottomSheetDialog.dismiss()
                // Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG).show();

            }
        })
    }

}
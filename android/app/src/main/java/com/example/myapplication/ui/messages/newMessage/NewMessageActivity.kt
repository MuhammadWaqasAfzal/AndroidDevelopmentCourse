package com.example.myapplication.ui.messages.newMessage

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphaSquared.wifapp.common.Constants
import com.example.login.Data
import com.example.login.GeneralResponse
import com.example.login.Users
import com.example.myapplication.R
import com.example.myapplication.api.MyApi
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.models.GetMessagesList
import com.example.myapplication.models.SendMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NewMessageActivity : AppCompatActivity() {
    var ivBack: ImageView? = null
    lateinit var activity: Activity
    lateinit var spinner: ConstraintLayout

    var recyclerView: RecyclerView? = null
    var adapter: AllUsersAdapter? = null
    var messagesData: ArrayList<Data>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        activity = this;
        spinner = findViewById(R.id.spinner);

        ivBack = findViewById(R.id.ivBack);
        ivBack!!.visibility = View.VISIBLE

        ivBack!!.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        loader(activity, spinner, true)
        callApiToGetAllUsers();
        initRecyclerView()

    }

    fun callApiToGetAllUsers() {
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
        var data = GetMessagesList(getSpObject(activity)?.getString(Constants.Id,"0")!!);
        //   var data = GetMessagesList("1");
        val call = api.getAllUsers(data)
        call?.enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                loader(activity,spinner, false);
                if (response.code() == 200) {
                    val res = response.body()
                    messagesData = res?.data
                    adapter?.updateData(messagesData)
                    // adapter?.notifyDataSetChanged()
                } else
                    showSnackBar(activity, response.body()?.message.toString());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                loader(activity,spinner, false);
                showSnackBar(activity, activity.getString(R.string.error_general));

            }
        })

    }

    private fun initRecyclerView() {
        recyclerView = findViewById(R.id.rvUsers)
        // this creates a vertical layout Manager
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        adapter = AllUsersAdapter(messagesData!!, activity) { user, position, action ->
            onMessageClick(
                user,
                position,
                action
            )
        }
        recyclerView!!.adapter = adapter
    }

    private fun onMessageClick(user: Data, position: Int, action: String) {
        showNewMessageBottomDialog(user,position);

    }

    private fun showNewMessageBottomDialog(user: Data, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.send_message_bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.SheetDialog)
        bottomSheetDialog.setContentView(dialogView)

        val btSubmit = bottomSheetDialog.findViewById<Button>(R.id.btSubmit);
        val edText = bottomSheetDialog.findViewById<EditText>(R.id.edText);
        val spinner = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.spinner);


        btSubmit?.setOnClickListener {
            if (!edText?.text.isNullOrBlank()) {
                spinner?.visibility = View.VISIBLE
                bottomSheetDialog.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                callApiToSendMessage(
                    bottomSheetDialog,
                    edText?.text.toString(),
                    spinner,
                    user,
                    position
                )
            } else
                showSnackBar(activity, getString(R.string.lbl_text))

        }


        bottomSheetDialog.show()
    }

    private fun callApiToSendMessage(bottomSheetDialog: BottomSheetDialog, text: String, spinner: ConstraintLayout?, user: Data, position: Int) {
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
        var data = SendMessage(text,getSpObject(activity)?.getString(Constants.Id,"0")!!.toInt(),user.Id?.toInt());
        //   var data = GetMessagesList("1");
        val call = api.sendMessage(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                bottomSheetDialog.dismiss()
                loader(activity,spinner!!, false);
                if (response.code() == 200) {
                    showSnackBar(activity, response.body()?.message.toString());
                } else
                    showSnackBar(activity, response.body()?.message.toString());
                bottomSheetDialog.dismiss()
                activity.finish()
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                loader(activity, spinner!!, false);
                bottomSheetDialog.dismiss()
                showSnackBar(activity, activity.getString(R.string.error_general));
                activity.finish();

            }
        })
    }

}
package com.example.myapplication.ui.messages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.Data
import com.example.myapplication.Messages
import com.example.myapplication.api.MyApi
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.FragmentMessagesBinding
import com.example.myapplication.models.GetMessagesList
import com.example.myapplication.ui.messages.newMessage.NewMessageActivity
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MessagesFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null

    var recyclerView: RecyclerView? = null
    var adapter: MessagesAdapter? = null
    var messagesData: ArrayList<Data>? = ArrayList()
    lateinit var activity: Activity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity = requireActivity()
        loader(activity, binding.spinner.llLoader, true)
        callApiToGetAllMessages();
        initRecyclerView()
        Constants.fab.setOnClickListener {
            Toast.makeText(activity,"waqas",Toast.LENGTH_LONG).show();
           startActivity(Intent(activity, NewMessageActivity::class.java))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun callApiToGetAllMessages() {
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
         var data = GetMessagesList(getSpObject(activity)?.getString(Constants.Id,"0")!!);
      //   var data = GetMessagesList("1");
        val call = api.getAllMessages(data)
        call?.enqueue(object : Callback<Messages> {
            override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
                loader(activity, binding.spinner.llLoader, false);
                if (response.code() == 200) {
                    val res = response.body()
                    messagesData = res?.data
                    adapter?.updateData(messagesData)
                    // adapter?.notifyDataSetChanged()
                } else
                    showSnackBar(activity, response.body()?.message.toString());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Messages>, t: Throwable) {
                loader(activity, binding.spinner.llLoader, false);
                showSnackBar(activity, activity.getString(com.example.myapplication.R.string.error_general));

            }
        })

    }

    private fun initRecyclerView() {
        recyclerView = binding.rvMessages
        // this creates a vertical layout Manager
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        adapter = MessagesAdapter(messagesData!!, activity) { user, position, action ->
            onMessageClick(
                user,
                position,
                action
            )
        }
        recyclerView!!.adapter = adapter
    }

    private fun onMessageClick(user: Data, position: Int, action: String) {


    }

}
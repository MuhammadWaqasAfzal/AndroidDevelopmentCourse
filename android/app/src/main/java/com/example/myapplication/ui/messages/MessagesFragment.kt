package com.example.myapplication.ui.messages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.Data
import com.example.myapplication.Messages
import com.example.myapplication.R
import com.example.myapplication.api.MyApi
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.FragmentMessagesBinding
import com.example.myapplication.models.GetMessagesList
import com.example.myapplication.ui.messages.newMessage.NewMessageActivity
import okhttp3.Connection
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
    var layoutPlaceHolder: LinearLayout? = null

    var adapter: MessagesAdapter? = null
    var messagesData: MutableList<Data>? = ArrayList()
    var messagesFilterData: MutableList<Data>? = ArrayList()

    lateinit var activity: Activity

    lateinit var swipeToRefreshProfile: SwipeRefreshLayout
    lateinit var simpleSearchView: SearchView

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
        layoutPlaceHolder = binding.layoutPlaceHolder
        activity = requireActivity()

        swipeToRefreshProfile = binding.swipeToRefreshProfile
        simpleSearchView = binding.simpleSearchView

        loader(activity, binding.spinner.llLoader, true)


        if(getSpObject(activity)?.getString(Constants.isAdmin,"0")=="1")
            Constants.fab.visibility = View.VISIBLE
        else
            Constants.fab.visibility = View.GONE
        Constants.fab.setOnClickListener {
            Toast.makeText(activity, "waqas", Toast.LENGTH_LONG).show();
            startActivity(Intent(activity, NewMessageActivity::class.java))
        }



        swipeToRefreshProfile.setOnRefreshListener {
            callApiToGetAllMessages()
        }

        setSearchBar()

        return root
    }

    private fun setSearchBar() {
        simpleSearchView.setQueryHint(getString(R.string.search_hint_message));
        simpleSearchView.setIconifiedByDefault(false);
        simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                messagesFilterData?.clear()
                for (review in messagesData!!) {
                    if (review.SenderName.toString().toLowerCase()
                            .contains(newText.toString().toLowerCase()) || review.ReceiverName.toString().toLowerCase()
                            .contains(newText.toString().toLowerCase())
                    ) {
                        messagesFilterData?.add(review)
                    }
                }
                adapter?.updateData(messagesFilterData);
                return false
            }
        })

    }


    override fun onResume() {
        super.onResume()
        initRecyclerView()

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
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(MyApi::class.java);
        var data = GetMessagesList(getSpObject(activity)?.getString(Constants.Id, "0")!!);
        //   var data = GetMessagesList("1");
        val call = api.getAllMessages(data)
        call?.enqueue(object : Callback<Messages> {
            override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
                loader(activity, binding.spinner.llLoader, false);
                swipeToRefreshProfile.isRefreshing = false
                if (response.code() == 200) {
                    val res = response.body()
                    if (res?.data!!.size > 0) {
                        layoutPlaceHolder?.visibility = View.GONE
                        adapter?.updateData(messagesData)

                        messagesData = res.data.toMutableList()
                        messagesFilterData = res.data.toMutableList()
                        adapter?.updateData(messagesFilterData)

                    } else
                        layoutPlaceHolder?.visibility = View.VISIBLE
                    // adapter?.notifyDataSetChanged()
                } else
                    showSnackBar(activity, response.body()?.message.toString());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Messages>, t: Throwable) {
                swipeToRefreshProfile.isRefreshing = false
                loader(activity, binding.spinner.llLoader, false);
                showSnackBar(
                    activity,
                    activity.getString(com.example.myapplication.R.string.error_general)
                );

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
        callApiToGetAllMessages();
    }

    private fun onMessageClick(user: Data, position: Int, action: String) {


    }

}
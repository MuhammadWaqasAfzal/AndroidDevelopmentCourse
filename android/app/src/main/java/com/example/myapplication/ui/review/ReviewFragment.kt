package com.example.myapplication.ui.review

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alphaSquared.wifapp.common.Constants
import com.example.login.GeneralResponse
import com.example.myapplication.R
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.FragmentHomeBinding
import com.example.myapplication.models.EditReview
import com.example.myapplication.models.LikeDisLikeReview
import com.example.myapplication.ui.api.MyApi
import com.example.review.Data
import com.example.review.Review
import com.google.android.material.bottomsheet.BottomSheetDialog
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ReviewFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    var recyclerView: RecyclerView? = null
    var adapter: ReviewsAdapter? = null
    var reviewsData: ArrayList<Data>? = ArrayList()
    lateinit var activity: Activity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity = requireActivity()
        Constants.reviewFragment = this
        loader(activity, binding.spinner.llLoader, true)
       // refreshReviewsList(this)
        callApiToGetReviews();
        initRecyclerView()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun callApiToGetReviews() {
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
        // var loginData = LoginData(edEmail?.text.toString().trim().toLowerCase(),edPassword?.text.toString());
        val call = api.getAllReview()
        call?.enqueue(object : Callback<Review> {
            override fun onResponse(call: Call<Review>, response: Response<Review>) {
                loader(activity, binding.spinner.llLoader, false);
                if (response.code() == 200) {
                    val res = response.body()
                    reviewsData = res?.data
                    adapter?.updateData(reviewsData)
                    // adapter?.notifyDataSetChanged()
                } else
                    showSnackBar(activity, response.body()?.message.toString());
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<Review>, t: Throwable) {
                loader(activity, binding.spinner.llLoader, false);
                showSnackBar(activity, activity.getString(R.string.error_general));

            }
        })

    }

    private fun initRecyclerView() {
        recyclerView = binding.rvReviews
        // this creates a vertical layout Manager
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        adapter = ReviewsAdapter(reviewsData!!, activity) { user, position, action ->
            onReviewClick(
                user,
                position,
                action
            )
        }
        recyclerView!!.adapter = adapter
    }

    private fun onReviewClick(user: Data, position: Int, action: String) {

        if (action.equals(Constants.Like)) {
            callAPiToLikeReview(user, position)
        } else if (action.equals(Constants.DisLike)) {
            callAPiToDisLikeReview(user, position)
        } else if (action.equals(Constants.Edit)) {
            showEditReviewBottomDialog(user, position)
        }
    }

    private fun showEditReviewBottomDialog(user: Data, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.create_review_bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.SheetDialog)
        bottomSheetDialog.setContentView(dialogView)

        val btSubmit = bottomSheetDialog.findViewById<Button>(R.id.btSubmit);
        val edDescription = bottomSheetDialog.findViewById<EditText>(R.id.edDescription);
        val spinner = bottomSheetDialog.findViewById<ConstraintLayout>(R.id.spinner);
        val tvTitle = bottomSheetDialog.findViewById<TextView>(R.id.tvTitle);
        val tvHeading = bottomSheetDialog.findViewById<TextView>(R.id.tvHeading);

        tvTitle?.text = getString(R.string.edit_review)
        tvTitle?.text = getString(R.string.edit_review)
        tvHeading?.text = getString(R.string.description)
        edDescription?.setText(user.Description.toString())

        btSubmit?.setOnClickListener {
            if (!edDescription?.text.isNullOrBlank()) {
                spinner?.visibility = View.VISIBLE
                bottomSheetDialog.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
                callApiToEditReview(
                    bottomSheetDialog,
                    edDescription?.text.toString(),
                    spinner,
                    user,
                    position
                )
            } else
                showSnackBar(activity, getString(R.string.lbl_description))

        }


        bottomSheetDialog.show()
    }

    private fun callApiToEditReview(
        bottomSheetDialog: BottomSheetDialog,
        description: String,
        spinner: ConstraintLayout?,
        user: Data,
        position: Int
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
        var data = EditReview(user.Email.toString(), user.Id?.toInt(), description);
        val call = api.editReview(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                loader(activity, spinner!!, false);
                bottomSheetDialog.dismiss()
                showSnackBar(activity, response.message());
                if (response.code() == 200) {
                    user.Description = description
                    adapter?.updateSingleCell(user, position)
                }
                // Toast.makeText(this@SignInActivity,res?.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                loader(activity, spinner!!, false);
                showSnackBar(activity, activity.getString(R.string.error_general));
                bottomSheetDialog.dismiss()
                // Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG).show();

            }
        })
    }


    private fun callAPiToDisLikeReview(user: Data, position: Int) {
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
        var data = LikeDisLikeReview(user.Email.toString(), user.Id?.toInt());
        val call = api.dislikeReview(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                if (response.code() == 200) {
                    val res = response.body()
                    user.Dislikes = (user.Dislikes!!.toInt() + 1).toString()
                    adapter?.updateSingleCell(user, position)
                }
                showSnackBar(activity, response.body()?.message.toString());
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                showSnackBar(activity, activity.getString(R.string.error_general));
            }
        })

    }

    private fun callAPiToLikeReview(user: Data, position: Int) {
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
        var data = LikeDisLikeReview(user.Email.toString(), user.Id?.toInt());
        val call = api.likeReview(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                showSnackBar(activity, response.body()?.message.toString());
                if (response.code() == 200) {
                    val res = response.body()
                    user.Likes = (user.Likes!!.toInt() + 1).toString()
                    adapter?.updateSingleCell(user, position)
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                showSnackBar(activity, activity.getString(R.string.error_general));
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }
}
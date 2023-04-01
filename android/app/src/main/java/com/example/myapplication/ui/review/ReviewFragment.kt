package com.example.myapplication.ui.review

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.alphaSquared.wifapp.common.Constants
import com.example.login.GeneralResponse
import com.example.login.ReviewReaction
import com.example.myapplication.R
import com.example.myapplication.api.MyApi
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.loader
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.FragmentReviewBinding
import com.example.myapplication.models.CreateReview
import com.example.myapplication.models.DeleteReview
import com.example.myapplication.models.EditReview
import com.example.myapplication.models.LikeDisLikeReview
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

    private var _binding: FragmentReviewBinding? = null

    var recyclerView: RecyclerView? = null
    var layoutPlaceHolder: LinearLayout? = null
    var adapter: ReviewsAdapter? = null
    var reviewsData: MutableList<Data> = ArrayList()
    var reviewsFilterData: MutableList<Data>? = ArrayList()
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

        _binding = FragmentReviewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        activity = requireActivity()
        layoutPlaceHolder = binding.layoutPlaceHolder
        swipeToRefreshProfile = binding.swipeToRefreshProfile
        simpleSearchView = binding.simpleSearchView
        Constants.reviewFragment = this
        loader(activity, binding.spinner.llLoader, true)
        // refreshReviewsList(this)
        initRecyclerView()

        try {
            Handler().postDelayed({
                Constants.fab.setOnClickListener { view ->
                    showAddReviewBottomDialog()
                }
            }, 1000)
        } catch (e: Exception) {
            Handler().postDelayed({
                Constants.fab.setOnClickListener { view ->
                    showAddReviewBottomDialog()
                }
            }, 2000)
        }

        swipeToRefreshProfile.setOnRefreshListener {
            callApiToGetReviews();
        }

        setSearchBar()
        return root
    }

    private fun setSearchBar() {
        simpleSearchView.setQueryHint(getString(R.string.search_hint));
        simpleSearchView.setIconifiedByDefault(false);
        simpleSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                reviewsFilterData?.clear()
                for (review in reviewsData!!) {
                    if (review.Description.toString().toLowerCase()
                            .contains(newText.toString().toLowerCase()) || review.Id.toString()
                            .contains(newText.toString())
                    ) {
                        reviewsFilterData?.add(review)
                    }
                }
                adapter?.updateData(reviewsFilterData);
                return false
            }
        })

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(MyApi::class.java);
        val call = api.getAllReview()
        call?.enqueue(object : Callback<Review> {
            override fun onResponse(call: Call<Review>, response: Response<Review>) {
                loader(activity, binding.spinner.llLoader, false);
                swipeToRefreshProfile.isRefreshing = false
                if (response.code() == 200) {
                    val res = response.body()
                    if (res?.data!!.size > 0) {
                        layoutPlaceHolder?.visibility = View.GONE
                        reviewsData = res.data.toMutableList()
                        reviewsFilterData = res.data.toMutableList()
                        adapter?.updateData(reviewsFilterData)
                    } else
                        layoutPlaceHolder?.visibility = View.VISIBLE
                } else
                    showSnackBar(activity, response.body()?.message.toString());
            }

            override fun onFailure(call: Call<Review>, t: Throwable) {
                swipeToRefreshProfile.isRefreshing = false
                loader(activity, binding.spinner.llLoader, false);
                showSnackBar(activity, activity.getString(R.string.error_general));

            }
        })

    }

    private fun initRecyclerView() {
        recyclerView = binding.rvReviews
        // this creates a vertical layout Manager
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        adapter = ReviewsAdapter(reviewsFilterData!!, activity) { user, position, action ->
            onReviewClick(
                user,
                position,
                action
            )
        }
        recyclerView!!.adapter = adapter
        callApiToGetReviews();

    }

    private fun onReviewClick(user: Data, position: Int, action: String) {

        if (action.equals(Constants.Like) || action.equals(Constants.DisLike)) {
            callAPiToDisLikeReview(user, position, action)
        } else if (action.equals(Constants.DisLike)) {
            callAPiToDisLikeReview(user, position, action)
        } else if (action.equals(Constants.Edit)) {
            showEditReviewBottomDialog(user, position)
        } else if (action.equals(Constants.Delete)) {
            callAPiToDeleteReview(user, position)
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
            .baseUrl(Constants.BASE_URL)
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
                    adapter?.updateSingleCell(user, position, Constants.Edit)
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

    private fun callAPiToDeleteReview(user: Data, position: Int) {
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
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
        var data = DeleteReview(
            getSpObject(activity)!!.getString(Constants.Email, "-1").toString(),
            user.Id!!.toInt()
        );
        val call = api.deleteReview(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                if (response.code() == 200) {
                    val res = response.body()
                    //val user = res!!.data[0]
                    // user.Dislikes = (user.Dislikes!!.toInt() + 1).toString()
                    adapter?.deleteReview(position)
                }
                showSnackBar(activity, response.message());
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                showSnackBar(activity, activity.getString(R.string.error_general));
            }
        })

    }


    private fun callAPiToDisLikeReview(user: Data, position: Int, action: String) {
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
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

        var reaction = 0

        if (action.equals(Constants.Like)) {
            reaction = 1
        }


        val api = retrofit.create(MyApi::class.java);
        var data = LikeDisLikeReview(
            getSpObject(activity)!!.getString(Constants.Email, "-1").toString(),
            user.Id!!.toInt(),
            getSpObject(activity)!!.getString(Constants.Id, "-1")!!.toInt(),
            reaction
        );
        val call = api.likeDislikeReview(data)
        call?.enqueue(object : Callback<ReviewReaction> {
            override fun onResponse(
                call: Call<ReviewReaction>,
                response: Response<ReviewReaction>
            ) {
                if (response.code() == 200) {
                    val res = response.body()
                    val user = res!!.data[0]
                    // user.Dislikes = (user.Dislikes!!.toInt() + 1).toString()
                    adapter?.updateSingleCell(user, position, Constants.DisLike)
                }
                showSnackBar(activity, response.body()?.message.toString());
            }

            override fun onFailure(call: Call<ReviewReaction>, t: Throwable) {
                showSnackBar(activity, activity.getString(R.string.error_general));
            }
        })

    }

    private fun showAddReviewBottomDialog() {
        val dialogView = layoutInflater.inflate(R.layout.create_review_bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.SheetDialog)
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
            .baseUrl(Constants.BASE_URL)
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
                if (response.code() == 201) {
                    callApiToGetReviews()
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                loader(activity, spinner!!, false);
                showSnackBar(activity, activity.getString(R.string.error_general));
                bottomSheetDialog.dismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }
}
package com.example.myapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    var recyclerView: RecyclerView? = null
    var adapter: ReviewsAdapter? = null
    var reviewsData: ArrayList<ReviewsModel>? = ArrayList()
    var ivBack: ImageView? = null
    var toolbarTitle: TextView? = null

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

        // ivBack = binding.root.findViewById(R.id.ivBack)
        // ivBack!!.visibility = View.VISIBLE

        //  toolbarTitle = binding.root.findViewById(R.id.toolbarTitle);
        //  toolbarTitle!!.text = getString(R.string.lbl_home)

        //  ivBack!!.setOnClickListener {
        //super.onBackPressed();
        //   }
        setData();

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setData() {
        var data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Awais Afzal", "Good")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad  Afzal", "Satisfied")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Awais Afzal", "Good")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad  Afzal", "Satisfied")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Awais Afzal", "Good")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad  Afzal", "Satisfied")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        data = ReviewsModel("Muhammad Waqas Afzal", "Excellent")
        reviewsData!!.add(data)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView = binding.rvReviews
        // this creates a vertical layout Manager
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        adapter = ReviewsAdapter(reviewsData!!) { user, position , action -> onReviewClick(user, position,action) }
        recyclerView!!.adapter = adapter
    }

    private fun onReviewClick(user: ReviewsModel, position: Int, action: String) {
        Toast.makeText(activity, action, Toast.LENGTH_LONG).show()
    }
}
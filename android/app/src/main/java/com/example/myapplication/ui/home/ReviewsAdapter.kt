package com.example.myapplication.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ReviewsAdapter(
    private var ReviewsList: List<ReviewsModel>,
    private val onReviewClick: (review: ReviewsModel, position: Int, action: String) -> Unit
) :
    RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int {
        return ReviewsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reviews = ReviewsList[position]
        holder.bind(reviews, position, onReviewClick)
    }

    fun update(Reviews: ArrayList<ReviewsModel>) {
        ReviewsList = Reviews
        this.notifyDataSetChanged()
    }


    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_item_review_list, parent, false)) {

        var imageView: ImageView? = null
        var ivEdit: ImageView? = null
        var ivDislike: ImageView? = null
        var ivLike: ImageView? = null
        var tvUserName: TextView? = null
        var tvReviewDescription: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.ivAvatar)
            ivEdit = itemView.findViewById(R.id.ivEdit)
            ivDislike = itemView.findViewById(R.id.ivDislike)
            ivLike = itemView.findViewById(R.id.ivLike)
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvReviewDescription = itemView.findViewById(R.id.tvReviewDescription)
        }

        fun bind(
            review: ReviewsModel,
            position: Int,
            onReviewClick: (review: ReviewsModel, position: Int, action: String) -> Unit
        ) {
            tvUserName!!.text = review.userName
            tvReviewDescription!!.text = review.reviewDescription

            ivEdit!!.setOnClickListener {
                onReviewClick(review, position, "edit")
            }
            ivDislike!!.setOnClickListener {
                onReviewClick(review, position, "dislike")
            }
            ivLike !!. setOnClickListener {
                onReviewClick(review, position, "like")
            }
        }
    }
}
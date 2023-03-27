package com.example.myapplication.ui.review

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alphaSquared.wifapp.common.Constants
import com.example.myapplication.R
import com.example.review.Data

class ReviewsAdapter(
    private var ReviewsList: ArrayList<Data>,
    private var context:Activity,
    private val onReviewClick: (review: Data, position: Int, action: String) -> Unit
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
        holder.bind(reviews, position,context, onReviewClick)
    }

    fun update(Reviews: ArrayList<Data>) {
        ReviewsList = Reviews
        this.notifyDataSetChanged()
    }

    fun updateData(reviewsData: ArrayList<Data>?) {
        this.ReviewsList.clear()
        this.ReviewsList = reviewsData!!;
        this.notifyDataSetChanged()

    }

    fun updateSingleCell(user: Data, position: Int, like: String) {
        ReviewsList.set(position,user);
        this.notifyItemChanged(position)



    }


    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.single_item_review_list, parent, false)) {

        var imageView: ImageView? = null
        var ivEdit: ImageView? = null
        var ivDislike: ImageView? = null
        var ivLike: ImageView? = null
        var tvUserName: TextView? = null
        var tvReviewDescription: TextView? = null
        var tvDislikeCount: TextView? = null
        var tvlikeCount: TextView? = null

        init {
            imageView = itemView.findViewById(R.id.ivAvatar)
            ivEdit = itemView.findViewById(R.id.ivEdit)
            ivDislike = itemView.findViewById(R.id.ivDislike)
            ivLike = itemView.findViewById(R.id.ivLike)
            tvUserName = itemView.findViewById(R.id.tvUserName)
            tvReviewDescription = itemView.findViewById(R.id.tvReviewDescription)
            tvDislikeCount = itemView.findViewById(R.id.tvDislikeCount)
            tvlikeCount = itemView.findViewById(R.id.tvlikeCount)
        }

        fun bind(
            review: Data,
            position: Int,
            context: Activity,
            onReviewClick: (review: Data, position: Int, action: String) -> Unit
        ) {
            tvUserName!!.text = review.UserName
            tvReviewDescription!!.text = review.Description
            tvReviewDescription!!.text = review.Description
            tvlikeCount!!.text = review.Likes
            tvDislikeCount!!.text = review.Dislikes

            ivEdit!!.setOnClickListener {
                onReviewClick(review, position,Constants.Edit)
            }
            ivDislike!!.setOnClickListener {
                onReviewClick(review, position, Constants.DisLike)
            }
            ivLike !!. setOnClickListener {
                onReviewClick(review, position, Constants.Like)
            }
        }
    }
}
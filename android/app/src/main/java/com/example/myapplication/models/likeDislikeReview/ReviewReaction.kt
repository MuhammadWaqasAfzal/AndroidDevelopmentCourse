package com.example.login

import com.example.review.Data
import com.google.gson.annotations.SerializedName


data class ReviewReaction (

  @SerializedName("status"  ) var status  : Int?    = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)



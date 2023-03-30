package com.example.review

import com.example.myapplication.models.review.Reactions
import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("Id"          ) var Id          : String? = null,
  @SerializedName("Description" ) var Description : String? = null,
  @SerializedName("Email"       ) var Email       : String? = null,
  @SerializedName("Likes"       ) var Likes       : String? = null,
  @SerializedName("Dislikes"    ) var Dislikes    : String? = null,
  @SerializedName("DateAndTime" ) var DateAndTime : String? = null,
  @SerializedName("UserName" ) var UserName : String? = null,
  @SerializedName("UserId" ) var UserId : String? = null,
  @SerializedName("Reactions"   ) var Reactions   : ArrayList<Reactions> = arrayListOf()


)
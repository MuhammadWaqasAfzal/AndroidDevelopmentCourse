package com.example.login

import com.google.gson.annotations.SerializedName


data class Users (

  @SerializedName("status"  ) var status  : Int?    = null,
  @SerializedName("message" ) var message : String? = null,
  @SerializedName("data"    ) var data    : ArrayList<Data>   = arrayListOf()

)



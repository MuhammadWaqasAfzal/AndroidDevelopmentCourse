package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class Messages (

  @SerializedName("status"  ) var status  : Int?            = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf()

)
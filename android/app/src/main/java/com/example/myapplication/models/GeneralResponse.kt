package com.example.login

import com.google.gson.annotations.SerializedName


data class GeneralResponse (

  @SerializedName("status"  ) var status  : Int?    = null,
  @SerializedName("message" ) var message : String? = null,

)



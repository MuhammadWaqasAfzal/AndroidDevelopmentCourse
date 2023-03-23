package com.example.login

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("Id"        ) var Id        : String? = null,
  @SerializedName("FirstName" ) var FirstName : String? = null,
  @SerializedName("LastName"  ) var LastName  : String? = null,
  @SerializedName("Password"  ) var Password  : String? = null,
  @SerializedName("Email"     ) var Email     : String? = null,
  @SerializedName("Gender"     ) var Gender     : String? = null,
  @SerializedName("Admin"     ) var Admin     : String? = null

)
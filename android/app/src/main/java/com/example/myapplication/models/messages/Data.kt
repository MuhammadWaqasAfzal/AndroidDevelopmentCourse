package com.example.myapplication

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("Id"             ) var Id             : String? = null,
  @SerializedName("SenderId"       ) var SenderId       : String? = null,
  @SerializedName("ReceiverId"     ) var ReceiverId     : String? = null,
  @SerializedName("DateAndTime"    ) var DateAndTime    : String? = null,
  @SerializedName("TextMessage"    ) var TextMessage    : String? = null,
  @SerializedName("SenderName"     ) var SenderName     : String? = null,
  @SerializedName("SenderGender"   ) var SenderGender   : String? = null,
  @SerializedName("ReceiverName"   ) var ReceiverName   : String? = null,
  @SerializedName("ReceiverGender" ) var ReceiverGender : String? = null

)
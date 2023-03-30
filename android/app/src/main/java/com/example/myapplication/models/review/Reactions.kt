package com.example.myapplication.models.review

import com.google.gson.annotations.SerializedName

data class Reactions (

    @SerializedName("Id"       ) var Id       : String? = null,
    @SerializedName("UserId"   ) var UserId   : String? = null,
    @SerializedName("Reaction" ) var Reaction : String? = null,
    @SerializedName("ReviewId" ) var ReviewId : String? = null

)
package com.example.review

import com.google.gson.annotations.SerializedName


data class Review(

    @SerializedName("status") var status: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf()

)
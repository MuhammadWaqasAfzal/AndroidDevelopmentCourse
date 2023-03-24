package com.example.myapplication.ui.api

import com.example.login.GeneralResponse
import com.example.login.Login
import com.example.myapplication.models.*
import com.example.review.Review
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface MyApi {
    @POST("login.php")
    fun login(@Body data: LoginData): Call<Login>

    @POST("register.php")
    fun signUp(@Body data: SignUpData): Call<Login>

    @POST("resetPassword.php")
    fun resetPassword(@Body data: ResetPassword): Call<GeneralResponse>

    @POST("addReview.php")
    fun createReview(@Body data: CreateReview): Call<GeneralResponse>

    @POST("likeReview.php")
    fun likeReview(@Body data: LikeDisLikeReview): Call<GeneralResponse>

    @POST("disLikeReview.php")
    fun dislikeReview(@Body data: LikeDisLikeReview): Call<GeneralResponse>

    @POST("updateReview.php")
    fun editReview(@Body data: EditReview): Call<GeneralResponse>

    @GET("allReviews.php")
    fun getAllReview(): Call<Review>
}
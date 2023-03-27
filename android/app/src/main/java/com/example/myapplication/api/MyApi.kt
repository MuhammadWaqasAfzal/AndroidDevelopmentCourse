package com.example.myapplication.api

import com.example.login.GeneralResponse
import com.example.login.Login
import com.example.login.Users
import com.example.myapplication.Messages
import com.example.myapplication.models.*
import com.example.review.Review
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface MyApi {
    @POST("login.php")
    fun login(@Body data: LoginData): Call<Login>

    @POST("register.php")
    fun signUp(@Body data: SignUpData): Call<Login>

    @POST("resetPassword.php")
    fun resetPassword(@Body data: ResetPassword): Call<GeneralResponse>

    @POST("addReview.php")
    fun createReview(@Body data: CreateReview): Call<GeneralResponse>

    @POST("likeDislikeReview.php")
    fun likeDislikeReview(@Body data: LikeDisLikeReview): Call<GeneralResponse>

    @POST("updateReview.php")
    fun editReview(@Body data: EditReview): Call<GeneralResponse>

    @GET("allReviews.php")
    fun getAllReview(): Call<Review>

    @POST("allMessages.php")
    fun getAllMessages(@Body data: GetMessagesList): Call<Messages>

    @POST("allUsers.php")
    fun getAllUsers(@Body data: GetMessagesList): Call<Users>

    @POST("sendMessage.php")
    fun sendMessage(@Body data: SendMessage): Call<GeneralResponse>

}
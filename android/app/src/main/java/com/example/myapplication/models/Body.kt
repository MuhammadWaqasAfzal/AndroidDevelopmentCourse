package com.example.myapplication.models

import com.google.gson.annotations.SerializedName


data class LoginData(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String?,
)

data class SignUpData(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String?,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("confirmPassword") val confirmPassword: String?,
    @SerializedName("gender") val gender: Int?,
    @SerializedName("admin") val admin: Int?
)

data class ResetPassword(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String?,
    @SerializedName("confirmPassword") val confirmPassword: String?,
)

data class CreateReview(
    @SerializedName("email") val email: String,
    @SerializedName("description") val password: String?,
)

data class EditReview(
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: Int?,
    @SerializedName("description") val password: String?,
)

data class DeleteReview(
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: Int?
)

data class LikeDisLikeReview(
    @SerializedName("email") val email: String,
    @SerializedName("reviewId") val reviewId: Int?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("reaction") val reaction: Int?,
)

data class GetMessagesList(
    @SerializedName("userId") val userId: String
)

data class SendMessage(
    @SerializedName("text") val text: String,
    @SerializedName("senderId") val senderId: Int?,
    @SerializedName("receiverId") val receiverId: Int?
)

data class EditUserName(
    @SerializedName("id") val text: String,
    @SerializedName("firstName") val senderId: String?,
    @SerializedName("lastName") val receiverId: String?
)
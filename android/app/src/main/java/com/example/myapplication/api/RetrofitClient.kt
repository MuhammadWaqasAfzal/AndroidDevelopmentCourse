package com.example.myapplication.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient private constructor() {
    fun getClient(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    companion object {
        private const val BASE_URL = "http://localhost:8090/v1/"
    }
}

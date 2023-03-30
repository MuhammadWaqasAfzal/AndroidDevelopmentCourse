package com.alphaSquared.wifapp.common

import com.example.myapplication.ui.review.ReviewFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton


class Constants {
    companion object {
        val SPLASH_TIME: Long = 3000
        const val Email = "Email";
        const val Id = "Id";
        const val FirstName = "FirstName";
        const val LastName = "LastName";
        const val Gender = "Gender";
        const val Password = "Password";
        const val LoggedIn = "LoggedIn";
        const val Edit = "Edit";
        const val Like = "Like";
        const val DisLike = "DisLike";

        lateinit var reviewFragment:ReviewFragment ;
        lateinit var fab:FloatingActionButton ;


        const val BASE_URL = "http://localhost:3000/"

    }
}
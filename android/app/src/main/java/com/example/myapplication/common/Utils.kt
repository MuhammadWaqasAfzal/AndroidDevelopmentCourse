package com.example.myapplication.common

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.ui.review.ReviewFragment
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Matcher
import java.util.regex.Pattern


fun showSnackBar(view: View, message: Int = R.string.error_general) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
}

fun isEmailValid(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
    //val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,}$"
    val passwordRegex = "^(?=.*[0-9])(?=.*[A-Za-z])(?=\\S+$).{6,}$"
    pattern = Pattern.compile(passwordRegex)
    matcher = pattern.matcher(password)
    return matcher.matches()
}

fun isInternetConnected(context: Context): Boolean {
    val connectionManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val wifi_Connection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    val mobile_data_connection = connectionManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

    if (wifi_Connection!!.isConnectedOrConnecting) {
        return true;
        // showToast(context,"WIFI Connection is on")
    } else {
        if (mobile_data_connection!!.isConnectedOrConnecting) {
            return true;
            //showToast(context,"Mobile Data Connection is on")
        } else {
            showSnackBar(context,context.getString(R.string.lbl_no_internet));
            return false;
        }
    }
}



fun loader(myActivityReference: Activity, spinner: ConstraintLayout, show: Boolean) {
    if (show) {
        spinner.visibility = View.VISIBLE
        myActivityReference.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    } else {
        spinner.visibility = View.GONE
        myActivityReference.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}


fun getSpObject(context: Context): SharedPreferences? {
    return context.getSharedPreferences("REVIEWS_APP", Context.MODE_PRIVATE)
    // var editor = sharedPreference.edit()
    //return editor

}


fun showSnackBar(context: Context,message:String) {
    val snackbar = Snackbar.make(
        (context as Activity).window.decorView,
     message,
        Snackbar.LENGTH_LONG
    )
        .setAction("Action", null)
    snackbar.setBackgroundTint(ContextCompat.getColor(context, R.color.buttonBackgroundColor))
    snackbar.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
    snackbar.show();
}

fun refreshReviewsList(reviewFragment: ReviewFragment)
{
    reviewFragment.callApiToGetReviews()

}



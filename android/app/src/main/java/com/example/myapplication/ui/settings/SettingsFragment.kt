package com.example.myapplication.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.alphaSquared.wifapp.common.Constants
import com.example.login.GeneralResponse
import com.example.myapplication.R
import com.example.myapplication.api.MyApi
import com.example.myapplication.common.getSpObject
import com.example.myapplication.common.isInternetConnected
import com.example.myapplication.common.setHeaderValues
import com.example.myapplication.common.showSnackBar
import com.example.myapplication.databinding.FragmentSettingsBinding
import com.example.myapplication.databinding.LayoutBottomChangeLanguageBinding
import com.example.myapplication.models.EditUserName
import com.example.myapplication.ui.registration.NewPasswordActivity
import com.example.myapplication.ui.registration.SplashActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.progressindicator.CircularProgressIndicator
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var tvChangeLanguage: TextView? = null
    private var tvUserName: TextView? = null
    private var tvUserEmail: TextView? = null
    private var tvChangePassword: TextView? = null
    private var ivEditName: ImageView? = null
    private var tvLogout: TextView? = null
    lateinit var activity: Activity
    var spinner: CircularProgressIndicator? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tvChangeLanguage = binding.tvChangeLanguage
        tvUserName = binding.tvUserName
        tvUserEmail = binding.tvUserEmail
        tvLogout = binding.tvLogout
        ivEditName = binding.ivEditName
        tvChangePassword = binding.tvChangePassword
        activity = requireActivity()
        spinner = this.binding.spinner

        setUserNameAndEmail()



        tvChangeLanguage?.setOnClickListener {
            openLanguageDialog()
        }
        tvChangePassword?.setOnClickListener {
            startActivity(Intent(activity, NewPasswordActivity::class.java))
        }
        tvLogout?.setOnClickListener {
            var sp = getSpObject(activity)?.edit()
            sp?.putBoolean(Constants.LoggedIn, false)
            sp?.commit()
            startActivity(Intent(activity, SplashActivity::class.java))
            activity.finishAffinity()
        }
        ivEditName?.setOnClickListener {
            openEditUserNameDialog();
        }
        // val textView: TextView = binding.textSlideshow

        return root
    }

    private fun setUserNameAndEmail() {
        var firstName = getSpObject(activity)?.getString(Constants.FirstName,"")
        var lastName = getSpObject(activity)?.getString( Constants.LastName,"")
        tvUserName?.text = firstName + " " +lastName
        tvUserEmail?.text = getSpObject(activity)?.getString(Constants.Email,"")

    }

    private fun openEditUserNameDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_user_name_bottom_dialog, null)
        val bottomSheetDialog = BottomSheetDialog(activity, R.style.SheetDialog)
        bottomSheetDialog.setContentView(dialogView)

        val btSubmit = bottomSheetDialog.findViewById<Button>(R.id.btSubmit);
        val edFirstName = bottomSheetDialog.findViewById<EditText>(R.id.edFirstName);
        val edLastName = bottomSheetDialog.findViewById<EditText>(R.id.edLastName);

        btSubmit?.setOnClickListener {
            if (isFieldsCorrect(edFirstName, edLastName) && isInternetConnected(activity)) {
                bottomSheetDialog.dismiss()
                spinner?.visibility = View.VISIBLE
                bottomSheetDialog.getWindow()?.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );

                callApi(edFirstName?.text.toString(),edLastName?.text.toString())
            }

        }


        bottomSheetDialog.show()
    }

    private fun isFieldsCorrect(edFirstName: EditText?, edLastName: EditText?): Boolean {
        when {
            edFirstName?.text.toString() == "" -> {
                edFirstName?.error = getString(R.string.first_name_required)
                edFirstName?.requestFocus()
                return false
            }
            edLastName?.text.toString() == "" -> {
                edLastName?.error = getString(R.string.last_name_required)
                edLastName?.requestFocus()
                return false
            }
            else -> return true
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCurrentLanguage(): String? {
        return getSpObject(activity)?.getString(Constants.Language, Constants.en);
    }

    private fun openLanguageDialog() {
        val dialogView = LayoutBottomChangeLanguageBinding.inflate(layoutInflater)
        val bottomSheetDialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
        bottomSheetDialog.setContentView(dialogView.root)

        if (getCurrentLanguage().equals("en")) {
            dialogView.tvEnglish.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick, 0, 0, 0)
            //dialogView.tvArabic.setCompoundDrawablesWithIntrinsicBounds(null, 0, 0, 0);
        } else
            dialogView.tvArabic.setCompoundDrawablesWithIntrinsicBounds(R.drawable.tick, 0, 0, 0)


        dialogView.llEnglish.setOnClickListener {
            if (!getCurrentLanguage().equals(Constants.en)) {
                bottomSheetDialog.dismiss()
                var sp = getSpObject(activity)?.edit()
                sp?.putString(Constants.Language, Constants.en)
                sp?.commit()
                //  LocaleHelper.setLocale(activity, Constants.en);

                startActivity(Intent(activity, SplashActivity::class.java))
                activity.finishAffinity()


            }
        }


        dialogView.llSpanisj.setOnClickListener {
            if (getCurrentLanguage().equals(Constants.en)) {
                bottomSheetDialog.dismiss()
                var sp = getSpObject(activity)?.edit()
                sp?.putString(Constants.Language, Constants.sp)
                sp?.commit()
                //    LocaleHelper.setLocale(activity, Constants.sp);

                startActivity(Intent(activity, SplashActivity::class.java))
                activity.finishAffinity()


            }
        }

        bottomSheetDialog.show()
    }

    private fun callApi(firstName: String, lastName: String) {
        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(16, TimeUnit.SECONDS)
            .readTimeout(16, TimeUnit.SECONDS)
            .writeTimeout(16, TimeUnit.SECONDS)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val api = retrofit.create(MyApi::class.java);
        var data = EditUserName(
            getSpObject(activity)?.getString(Constants.Id, "id").toString(),
            firstName,
            lastName
        );
        val call = api.editUserName(data)
        call?.enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(
                call: Call<GeneralResponse>,
                response: Response<GeneralResponse>
            ) {
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                spinner?.visibility = View.GONE
                showSnackBar(activity, response.message());
                if(response.code()==200)
                {
                    var sp = getSpObject(activity)?.edit()
                    sp?.putString(Constants.FirstName, firstName)
                    sp?.putString(Constants.LastName,lastName)
                    sp?.commit()
                    setUserNameAndEmail()
                    setHeaderValues(activity)

                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                spinner?.visibility = View.GONE
                activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                showSnackBar(activity, resources.getString(R.string.error_general));


            }
        })
    }

}
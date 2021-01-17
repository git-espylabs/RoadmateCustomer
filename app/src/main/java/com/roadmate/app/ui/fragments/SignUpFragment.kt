package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.CheckOtpActivity
import com.roadmate.app.ui.activities.LoginActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.fragment_signup.spin_kit
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SignUpFragment: Fragment(), View.OnClickListener{

    private fun signUpJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("name", edt_username.text.toString())
            json.put("phone", edt_number.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun isInputsValid() : Boolean{
        if (edt_username.text.toString() == ""){
            edt_username.error = "Enter Username"
            return false
        }else if (edt_number.text.toString() == ""){
            edt_number.error = "Enter mobile number"
            return false
        }else if (!chkBox1.isChecked){
            activity?.toast {
                message = "Accept the terms and conditions"
                duration = Toast.LENGTH_LONG
            }
            return false
        }
        return true
    }

    private fun processSignUp(){
        if (isInputsValid()){
            showProgress(true)
            lifecycleScope.launch{
                val response =   APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    customerSignUp(signUpJsonRequest())
                }

                if (response.isSuccessful && response.body()?.message.equals("Success")){
                    AppSession.userMobile = edt_number.text.toString()
                    AppSession.otpTemp = response.body()?.data!!
                    moveToOtpCheck()
                }
            }
        }
    }

    private fun moveToLogin(){
        activity?.startActivity<LoginActivity>()
        activity?.finish()
    }

    private fun moveToOtpCheck(){
        UserDetails().isUserRegistered = true
        activity?.startActivity<CheckOtpActivity>()
        activity?.finish()
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            btn_user_signUp.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
        }else{
            btn_user_signUp.visibility = View.VISIBLE
            spin_kit.visibility = View.GONE
        }
    }

    private fun openTermsAndConditions(){
        val dialogBuilder = AlertDialog.Builder(activity!!)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.terms_and_conditions, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        dialogView.findViewById<Button>(R.id.button).setOnClickListener { alertDialog.cancel() }
        alertDialog.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btn_user_signUp.setOnClickListener(this)
        login.setOnClickListener(this)
        tandclayout.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id){
            R.id.btn_user_signUp ->{
                NetworkUtils.isNetworkConnected(activity).doIfTrue { processSignUp() }elseDo {activity!!.toast {
                    message = "No Internet Connectivity!"
                    duration = Toast.LENGTH_LONG }
                }
            }
            R.id.login ->{
                moveToLogin()
            }
            R.id.tandclayout ->{
                openTermsAndConditions()
            }
            else -> null
        }
    }
}
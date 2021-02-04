package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerVehicleMaster
import com.roadmate.app.api.response.OtpMaster
import com.roadmate.app.api.response.OtpTrans
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.*
import com.roadmate.app.log.AppLogger
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.FcmDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.CustomerMainActivity
import com.roadmate.app.ui.activities.SignUpActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_check_otp.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CheckOtpFragment : Fragment(), View.OnClickListener {

    private fun otpJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
//            json.put("otp", otp_view.text.toString())
            json.put("phnum", AppSession.userMobile)
            json.put("otp", "5252")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun processVerification(){
        if (otp_view.text.toString() != ""){
            btnVerifyOtp.visibility = View.GONE
            spin_kit.visibility = View.VISIBLE
            lifecycleScope.launch{
                val  response = APIManager.call<ApiServices, Response<OtpMaster>> {
                    verifyOTP(otpJsonRequest())  }

                if (response.isSuccessful && response.body()?.message == "Success"){
                    UserDetails().isOtpVerified = true
                    getMyVehiclesList(response.body()?.data!![0])
                }
            }
        }else{
            activity?.toast {
                message = "Enter a valid OTP"
                duration = Toast.LENGTH_LONG
            }
        }
    }

    private fun getMyVehiclesList(data: OtpTrans){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<CustomerVehicleMaster>> {
                geMyVehicles(createRequestJson("userid", data.user_id))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                AppSession.myVehicleList = response.body()?.data!!
                if (AppSession.myVehicleList.isNotEmpty()){
                    setDefaultVehicle()
                }
            }
            initializeUserSettings(data)
        }
    }

    private fun createRequestJson(param: String, value: String): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, value)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun setDefaultVehicle(){
        DefaultVehicleDetails().vehicleId.isEmpty().doIfTrue {
            DefaultVehicleDetails().vehicleId = AppSession.myVehicleList[0].veh_id
            DefaultVehicleDetails().vehicleNo = AppSession.myVehicleList[0].vehicleNumber
            DefaultVehicleDetails().vehicleType = AppSession.myVehicleList[0].vehicleTypeName
            DefaultVehicleDetails().vehicleModelId = AppSession.myVehicleList[0].vehicleModelId
            DefaultVehicleDetails().vehicleModel = AppSession.myVehicleList[0].vehicleBrandName + " " + AppSession.myVehicleList[0].vehicleModelName
            DefaultVehicleDetails().vehicleFuelType = AppSession.myVehicleList[0].vehicleFuelType
        }
    }

    private fun registerForFcm(){
        if (FcmDetails().fcmToken != ""){
            FcmDetails().isFcmRegistered.doIfFalse {
                processFcmRegistration()
            }
        }else{
            requestFcmToke()
        }
    }

    private fun requestFcmToke(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            it.isSuccessful.doIfTrue {
                AppLogger.info("FCM Token", it.result!!.token)
                if (it.result!!.token != "") {
                    FcmDetails().fcmToken = it.result!!.token
                    processFcmRegistration()
                }
            }elseDo {
                AppLogger.info("FCM Token", "getInstanceId failed: " + it.exception)
                moveToHome()
            }
        })
    }

    private fun processFcmRegistration(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                registerForFcm(fcmRegistrationJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppLogger.info("FCM Registered")
            }
            moveToHome()
        }
    }

    private fun fcmRegistrationJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("fcm_token", FcmDetails().fcmToken)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun initializeUserSettings(data: OtpTrans){
        UserDetails().isUserLoggedIn = true
        UserDetails().userId = data.user_id
        UserDetails().userMobile = data.mobile
        UserDetails().userName = data.name
        moveToHome()
    }

    private fun moveToHome(){
        activity?.toast {
            message = "Phone number verified"
            duration = Toast.LENGTH_LONG
        }
        activity?.startActivity<CustomerMainActivity>()
        activity?.finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_check_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnVerifyOtp.setOnClickListener(this)
        otp_view.setText("5252")
        phone.text = getString(R.string.otp_prompt) + AppSession.userMobile
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnVerifyOtp ->{
                NetworkUtils.isNetworkConnected(activity).doIfTrue {
                    processVerification()
                }elseDo {
                    activity!!.toast {
                    message = "No Internet Connectivity!"
                    duration = Toast.LENGTH_LONG }}
            }
        }
    }
}
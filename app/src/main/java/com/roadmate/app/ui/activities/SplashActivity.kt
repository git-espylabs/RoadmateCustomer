package com.roadmate.app.ui.activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerVehicleMaster
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.constants.AppConstants.Companion.APP_STARTUP_DELAY
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.rmapp.AppSession.Companion.myVehicleList
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    private fun getMyVehiclesList(){
        try {
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<CustomerVehicleMaster>> {
                    geMyVehicles(createRequestJson("userid", UserDetails().userId))
                }
                if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                    myVehicleList = response.body()?.data!!
                    if (myVehicleList.isNotEmpty()){
                        setDefaultVehicle()
                    }
                }
                moveToNextScreen()
            }
        } catch (e: Exception) {
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
            DefaultVehicleDetails().vehicleId = myVehicleList[0].veh_id
            DefaultVehicleDetails().vehicleNo = myVehicleList[0].vehicleNumber
            DefaultVehicleDetails().vehicleType = myVehicleList[0].vehicleTypeName
            DefaultVehicleDetails().vehicleModelId = myVehicleList[0].vehicleModelId
            DefaultVehicleDetails().vehicleModel = myVehicleList[0].vehicleBrandName + " " + myVehicleList[0].vehicleModelName
            DefaultVehicleDetails().vehicleFuelType = myVehicleList[0].vehicleFuelType
        }
    }

    private fun moveToNextScreen(){
        Handler().postDelayed({
            if (UserDetails().isUserLoggedIn){
                this.startActivity<CustomerMainActivity>()
                this.finish()
            }else{
                this.startActivity<LoginActivity>()
                this.finish()
            }
        }, APP_STARTUP_DELAY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        supportActionBar!!.hide()

        UserDetails().isUserLoggedIn.doIfTrue {
            getMyVehiclesList()
        }elseDo {
            moveToNextScreen()
        }
    }
}
package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerVehicleMaster
import com.roadmate.app.api.response.CustomerVehicleTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.constants.AppConstants
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_customer_vehicle_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CustomerVehicleDetailsFragment: Fragment(), View.OnClickListener{

    private fun setListeners(){
        bookedPackage.setOnClickListener(this)
        clickbookedPackage.setOnClickListener(this)
        completedPackage.setOnClickListener(this)
        clickcompletedPackage.setOnClickListener(this)
    }

    private fun getVehicleDetails(){

        var myVehicleDetails: CustomerVehicleTrans? = null


        for (vehicle in AppSession.myVehicleList){
            if (arguments!!.getString("vehicle_id") == vehicle.veh_id){
                myVehicleDetails = vehicle
                break
            }
        }

        v_model.text = myVehicleDetails?.vehicleBrandName + " " + myVehicleDetails?.vehicleModelName
        v_number.text = myVehicleDetails?.vehicleNumber

        when{
            myVehicleDetails?.vehicleTypeName.equals(AppConstants.VEHICLE_TYPE_TWO_WHEELER, true) -> Picasso.with(context).load(R.drawable.two_wheeler).into(v_type)

            myVehicleDetails?.vehicleTypeName.equals(AppConstants.VEHICLE_TYPE_FOUR_WHEELER, true) -> Picasso.with(context).load(R.drawable.four_wheeler).into(v_type)

            myVehicleDetails?.vehicleTypeName.equals(AppConstants.VEHICLE_TYPE_THREE_WHEELER, true) -> Picasso.with(context).load(R.drawable.three_wheeler).into(v_type)

            else -> Picasso.with(context).load(R.drawable.heavy_vehicle).into(v_type)
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

    private fun prepareBookedPackageView(){
        bookedpackageHistory.visibility = View.VISIBLE
        completdpackageHistory.visibility = View.GONE

        bookedPackage.visibility = View.GONE
        completedPackage.visibility = View.VISIBLE
        clickbookedPackage.visibility = View.VISIBLE
        clickcompletedPackage.visibility = View.GONE
    }

    private fun prepareCompletedPackageView(){
        bookedpackageHistory.visibility = View.GONE
        completdpackageHistory.visibility = View.VISIBLE

        bookedPackage.visibility = View.VISIBLE
        completedPackage.visibility = View.GONE
        clickbookedPackage.visibility = View.GONE
        clickcompletedPackage.visibility = View.VISIBLE
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_vehicle_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        getVehicleDetails()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bookedPackage ->{

            }
            R.id.completedPackage ->{

            }
        }
    }
}
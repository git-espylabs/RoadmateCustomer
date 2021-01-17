package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.roadmate.app.R
import com.roadmate.app.adapter.DefaultVehicleAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerVehicleMaster
import com.roadmate.app.api.response.CustomerVehicleTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import kotlinx.android.synthetic.main.vehicle_sheet.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class VehicleListBottomSheet internal constructor(val vehicleSelectionHandler: (vehicleData: CustomerVehicleTrans) -> Unit): BottomSheetDialogFragment() {

    private fun getMyVehiclesList(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<CustomerVehicleMaster>> {
                geMyVehicles(createRequestJson("user_id", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                AppSession.myVehicleList.clear()
                AppSession.myVehicleList = response.body()?.data!!
                populateVehicleList()
            }
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

    private fun setDefaultVehicle(vehicleData: CustomerVehicleTrans){
        DefaultVehicleDetails().vehicleId = vehicleData.veh_id
        DefaultVehicleDetails().vehicleNo = vehicleData.vehicleNumber
        DefaultVehicleDetails().vehicleType = vehicleData.vehicleTypeName
        DefaultVehicleDetails().vehicleModelId = vehicleData.vehicleModelId
        DefaultVehicleDetails().vehicleModel = vehicleData.vehicleBrandName + " " + vehicleData.vehicleModelName
        DefaultVehicleDetails().vehicleFuelType = vehicleData.vehicleFuelType
    }

    private fun populateVehicleList(){
        vehicle_recycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        var vehAdapter = DefaultVehicleAdapter(activity!!, AppSession.myVehicleList, false){ vehicleData, _, _, _ ->
            setDefaultVehicle(vehicleData!!)
            vehicleSelectionHandler(vehicleData)
            this@VehicleListBottomSheet.dismiss()
        }
        vehicle_recycler.adapter = vehAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.vehicle_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        close.setOnClickListener { this@VehicleListBottomSheet.dismiss() }
        populateVehicleList()
    }
}
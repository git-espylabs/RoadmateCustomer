package com.roadmate.app.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.AlertDialogAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_BRAND_ITINERARY
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_FUEL_ITINERARY
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_MODEL_ITINERARY
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_ITINERARY
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.CustomerMainActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_add_customer_vehicle.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AddCustomerVehicleFragment: Fragment(), View.OnClickListener {

    var selectedVehicleType = ""
    var selectedVehicleTypeName = ""
    var selectedVehicleBrand = ""
    var selectedVehicleBrandName = ""
    var selectedVehicleModel = ""
    var selectedVehicleModelName = ""
    var selectedFuelType = ""
    var selectedFuelTypeName = ""

    var isVehicleEditing = false

    private fun setListeners(){
        v_type.setOnClickListener(this)
        v_brand.setOnClickListener(this)
        v_model.setOnClickListener(this)
//        v_fuel.setOnClickListener(this)
        save_vehicle.setOnClickListener(this)
    }

    private fun getVehicleItineraries(){
        showProgress(true)

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleTypeMaster>> {
                getVehicleTypes()
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppSession.vehicleTypeList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleBrands(vehTypeId: String){
        showProgress(true)
        v_model.text = "Select"
        v_fuel.text = "Select"

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleBrandMaster>> {
                getVehicleBrands(vehItineraryJsonRequest("vehtype", vehTypeId))
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppSession.vehicleBrandList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleModels(vehBrandId: String){
        showProgress(true)
        v_fuel.text = "Select"

        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleModelMaster>> {
                getVehicleModels(vehItineraryJsonRequest("brand", vehBrandId))
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppSession.vehicleModelList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleFuelTypes(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleFuelTypeMaster>> {
                getVehicleFuelTypes()
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                AppSession.vehicleFuelTypeList = response.body()?.data!!
            }
            showProgress(false)
        }
    }

    private fun getVehicleFuelTypeByModel(model_id:String){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<VehicleFuelTypeMaster>> {
                getVehicleFuelTypeByModel(vehItineraryJsonRequest("model_id", model_id))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty() && response.body()?.message == "Success"){
                var fueltype = response.body()?.data!![0].vehicleFuelType
                v_fuel.text = fueltype
                selectedFuelType = response.body()?.data!![0].vehicleFuelTypeId
                selectedFuelTypeName = response.body()?.data!![0].vehicleFuelType
            }
            showProgress(false)
        }
    }

    private fun isInputsValid(): Boolean{
        when {
            selectedVehicleType.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a vehicle Type"
                    duration = Toast.LENGTH_LONG  }
                return false
            }
            selectedVehicleBrand.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a vehicle Brand"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            selectedVehicleModel.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a vehicle Model"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            selectedFuelType.isEmpty() -> {
                activity!!.toast {
                    message = "Please select a fuel Type"
                    duration = Toast.LENGTH_LONG }
                return false
            }
            v_number.text.toString().isEmpty() -> {
                v_number.error = "Enter Vehicle Number"
                return false
            }
            else -> return true
        }

    }

    private fun saveNewVehicle(){
        isInputsValid().doIfTrue {
            showProgress(true)
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertCustomerNewVehicle(vehSaveJsonRequest())
                }
                if (response.isSuccessful && response.body()?.message == "Success"){
                    activity!!.toast {
                        message = "New Vehicle added"
                        duration = Toast.LENGTH_LONG }
                    AppSession.myVehicleList.add(CustomerVehicleTrans(
                        response.body()?.data!!,
                        v_number.text.toString().trim(),
                        selectedVehicleType,
                        selectedVehicleTypeName,
                        selectedVehicleBrand,
                        selectedVehicleBrandName,
                        selectedVehicleModel,
                        selectedVehicleModelName,
                        selectedFuelType
                    ))
                    if (DefaultVehicleDetails().vehicleId.isEmpty()){
                        DefaultVehicleDetails().vehicleId = response.body()?.data!!
                        DefaultVehicleDetails().vehicleNo = v_number.text.toString().trim()
                        DefaultVehicleDetails().vehicleModel = v_brand.text.toString().trim() + " " + v_model.text.toString().trim()
                        DefaultVehicleDetails().vehicleModelId = selectedVehicleModel
                        DefaultVehicleDetails().vehicleType = selectedVehicleTypeName
                        DefaultVehicleDetails().vehicleFuelType = selectedFuelTypeName
                    }
                    activity?.startActivity<CustomerMainActivity>()
                    activity?.finish()
                }else if (response.isSuccessful){
                    activity!!.toast {
                        message = response.body()?.message!!
                        duration = Toast.LENGTH_LONG }
                }
                showProgress(false)
            }
        }
    }

    private inline fun<reified T> getItineraryNamesList(inputList: ArrayList<T>): ArrayList<String>{
        var nameList: ArrayList<String> = arrayListOf()

        for (T in inputList){
            when (T) {
                is VehicleTypeTrans -> {
                    nameList.add(T.vehicleType)
                }
                is VehicleBrandTrans -> {
                    nameList.add(T.vehicleBrand)
                }
                is VehicleModelTrans -> {
                    nameList.add(T.vehicleModel)
                }
                is VehicleFuelTypeTrans -> {
                    nameList.add(T.vehicleFuelType)
                }
            }
        }

        return nameList
    }

    private fun populateVehicleItinerary(dialogTitle: String, itineraryList: ArrayList<String>, textViewItinerary: TextView, itineraryListType: Int){
        val dialog: AlertDialog
        val builder = AlertDialog.Builder(activity)
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.custom_alert_dialog, null)
        builder.setView(view)
        dialog = builder.create()
        val textView = view.findViewById<TextView>(R.id.alert_heading)
        textView.text = dialogTitle
        val recyclerView: RecyclerView = view.findViewById(R.id.customAlertRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        val adapter = AlertDialogAdapter(activity!!, itineraryList){position, itineraryName ->
            textViewItinerary.text = itineraryName
            when(itineraryListType){
                VEHICLE_TYPE_ITINERARY -> {
                    v_brand.text = "Select"
                    v_model.text = "Select"
                    v_fuel.text = "Select"
                    selectedVehicleType = AppSession.vehicleTypeList[position].vehicleTypeId
                    selectedVehicleTypeName = AppSession.vehicleTypeList[position].vehicleType
                    AppSession.vehicleBrandList.clear()
                    getVehicleBrands(selectedVehicleType)
                }

                VEHICLE_BRAND_ITINERARY -> {
                    v_model.text = "Select"
                    v_fuel.text = "Select"
                    selectedVehicleBrand = AppSession.vehicleBrandList[position].vehicleBrandId
                    selectedVehicleBrandName = AppSession.vehicleBrandList[position].vehicleBrand
                    AppSession.vehicleModelList.clear()
                    getVehicleModels(selectedVehicleBrand)
                }

                VEHICLE_MODEL_ITINERARY -> {
                    v_fuel.text = ""
                    selectedVehicleModel = AppSession.vehicleModelList[position].vehicleModelId
                    selectedVehicleModelName = AppSession.vehicleModelList[position].vehicleModel
                    AppSession.vehicleFuelTypeList.clear()
                    getVehicleFuelTypeByModel(selectedVehicleModel)
                }

                VEHICLE_FUEL_ITINERARY -> {
                    selectedFuelType = AppSession.vehicleFuelTypeList[position].vehicleFuelTypeId
                    selectedFuelTypeName = AppSession.vehicleFuelTypeList[position].vehicleFuelType
                }
            }
            dialog.dismiss()
        }
        recyclerView.adapter = adapter
        dialog.show()
    }

    private fun vehItineraryJsonRequest(param: String, vehTypeId: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, vehTypeId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun vehSaveJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("userid", UserDetails().userId)
            json.put("veh_number", v_number.text.toString().trim())
            json.put("veh_type", selectedVehicleType)
            json.put("veh_brand", selectedVehicleBrand)
            json.put("veh_model", selectedVehicleModel)
            json.put("fuel_type", selectedFuelType)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun clearAllItineraries(){
        AppSession.vehicleTypeList.clear()
        AppSession.vehicleBrandList.clear()
        AppSession.vehicleModelList.clear()
        AppSession.vehicleFuelTypeList.clear()
    }

    private fun editVehicle(){
        if (isVehicleEditing) {
            var myVehicle: CustomerVehicleTrans? = null
            for (vehicle in AppSession.myVehicleList){
                if (arguments!!.getString("vehicle_id") == vehicle.veh_id){
                    myVehicle = vehicle
                    break
                }
            }
            v_type.text = myVehicle?.vehicleTypeName
            v_brand.text = myVehicle?.vehicleBrandName
            v_model.text = myVehicle?.vehicleModelName
            v_fuel.text = myVehicle?.vehicleFuelType
            v_number.setText(myVehicle?.vehicleNumber)
        }
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
        return inflater.inflate(R.layout.fragment_add_customer_vehicle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        isVehicleEditing = !arguments!!.getString("vehicle_id").isNullOrEmpty()
        editVehicle()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            clearAllItineraries()
            getVehicleItineraries()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_LONG }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.v_type -> {
                AppSession.vehicleTypeList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Type",
                        getItineraryNamesList(AppSession.vehicleTypeList),
                        v_type,
                        VEHICLE_TYPE_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Service not available at this moment!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.v_brand -> {
                AppSession.vehicleBrandList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Brand",
                        getItineraryNamesList(AppSession.vehicleBrandList),
                        v_brand,
                        VEHICLE_BRAND_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Please select a vehicle type!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.v_model -> {
                AppSession.vehicleModelList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Model",
                        getItineraryNamesList(AppSession.vehicleModelList),
                        v_model,
                        VEHICLE_MODEL_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Please select a vehicle Brand!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.v_fuel -> {
                AppSession.vehicleFuelTypeList.isNotEmpty().doIfTrue {
                    populateVehicleItinerary(
                        "Select Vehicle Fuel Type",
                        getItineraryNamesList(AppSession.vehicleFuelTypeList),
                        v_fuel,
                        VEHICLE_FUEL_ITINERARY
                    )
                }elseDo {
                    activity!!.toast {
                        message = "Service not available at this moment!"
                        duration = Toast.LENGTH_LONG }
                }
            }

            R.id.save_vehicle -> {
                NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                    saveNewVehicle()
                }elseDo {
                    activity!!.toast {
                        message = "No Internet connectivity!"
                        duration = Toast.LENGTH_LONG }
                }
            }
        }
    }
}
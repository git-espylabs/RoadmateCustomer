package com.roadmate.app.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.DefaultVehicleAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerVehicleMaster
import com.roadmate.app.api.response.CustomerVehicleTrans
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.*
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_customer_account.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response




class CustomerAccountFragment: Fragment(),View.OnClickListener {

    private lateinit var vehAdapter: DefaultVehicleAdapter

    private fun setListeners(){
        editButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)
        cancelEditButton.setOnClickListener(this)
        logoutButton.setOnClickListener(this)
        addvehicle.setOnClickListener(this)
        transaction.setOnClickListener(this)
        addMyShop.setOnClickListener(this)
        inviteFriends.setOnClickListener(this)
        btnBookingHistory.setOnClickListener(this)
    }

    private fun getMyVehiclesList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<CustomerVehicleMaster>> {
                geMyVehicles(createRequestJson("userid", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                AppSession.myVehicleList.clear()
                AppSession.myVehicleList = response.body()?.data!!
                if (AppSession.myVehicleList.isNotEmpty()){
                    setDefaultVehicle()
                }
                populateVehicleList()
            }
            showProgress(false)
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

    private fun populateVehicleList(){
        my_vehicles.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        vehAdapter = DefaultVehicleAdapter(activity!!, AppSession.myVehicleList, true){vehicleData, position, isDelete, isEdit ->
            when {
                isDelete -> {
                    deleteVehicleFromServer(vehicleData!!, position)
                }
                /*else -> {
                    val intent = Intent(
                        activity,
                        CustomerVehicleDetailActivity::class.java
                    )
                    intent.putExtra("vehicle_id", vehicleData?.veh_id)
                    startActivity(intent)
                }*/
            }
        }
        my_vehicles.adapter = vehAdapter
    }

    private fun populateAccountDetails(){
        txtusername.text = UserDetails().userName
        txtnumber.text = UserDetails().userMobile
        edtusername.setText(UserDetails().userName)
        edtnumber.setText(UserDetails().userMobile)
    }

    private fun enableEditView(isEdit: Boolean){
        if (isEdit) {
            editButton.visibility = View.GONE
            saveButton.visibility = View.VISIBLE
            logoutButton.visibility = View.GONE
            cancelEditButton.visibility = View.VISIBLE
            txtusername.visibility = View.GONE
            txtnumber.visibility = View.GONE
            edtusername.visibility = View.VISIBLE
            edtnumber.visibility = View.VISIBLE
        } else {
            editButton.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
            logoutButton.visibility = View.VISIBLE
            cancelEditButton.visibility = View.GONE
            txtusername.visibility = View.VISIBLE
            txtnumber.visibility = View.VISIBLE
            edtusername.visibility = View.GONE
            edtnumber.visibility = View.GONE
        }
    }

    private fun deleteVehicleFromServer(obj: CustomerVehicleTrans, position: Int){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                deleteVehicle(createDeleteRequestJson("id", obj.veh_id))
            }
            if (response.isSuccessful && response.body()?.message!!.equals("success", true)){
                activity!!.toast {
                    message = "Vehicle removed"
                    duration = Toast.LENGTH_SHORT
                }
            }
            AppSession.myVehicleList.removeAt(position)
            if (DefaultVehicleDetails().vehicleId == obj.veh_id){
                if (AppSession.myVehicleList.isNotEmpty()){
                    DefaultVehicleDetails().vehicleId = AppSession.myVehicleList[0].veh_id
                    DefaultVehicleDetails().vehicleNo = AppSession.myVehicleList[0].vehicleNumber
                    DefaultVehicleDetails().vehicleType = AppSession.myVehicleList[0].vehicleTypeName
                    DefaultVehicleDetails().vehicleModelId = AppSession.myVehicleList[0].vehicleModelId
                    DefaultVehicleDetails().vehicleModel = AppSession.myVehicleList[0].vehicleModelName
                    DefaultVehicleDetails().vehicleFuelType = AppSession.myVehicleList[0].vehicleFuelType
                }else{
                    DefaultVehicleDetails().vehicleId = ""
                    DefaultVehicleDetails().vehicleNo = ""
                    DefaultVehicleDetails().vehicleType = ""
                    DefaultVehicleDetails().vehicleModel = ""
                    DefaultVehicleDetails().vehicleModelId = ""
                    DefaultVehicleDetails().vehicleFuelType = ""
                }
            }
            vehAdapter.notifyDataSetChanged()
            showProgress(false)
        }
    }

    private fun createDeleteRequestJson(param: String, value: String): RequestBody {
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

    private fun editUser(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                userEditData(userEditRequestJson())
            }
            if (response.isSuccessful && response.body()?.message!!.equals("success", true)){
                UserDetails().userName = edtusername.text.toString().trim()
                UserDetails().userMobile = edtnumber.text.toString().trim()
                populateAccountDetails()
                enableEditView(false)
                activity!!.toast {
                    message = "Details Saved"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun userEditRequestJson(): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("id", UserDetails().userId)
            json.put("name", edtusername.text.toString().trim())
            json.put("phnum", edtnumber.text.toString().trim())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(visible:Boolean){
        if (visible){
            loadingFrame.visibility = View.VISIBLE
        }else{
            loadingFrame.visibility = View.GONE
        }
    }

    private fun redirectToShopAppPlayStore(){
        val appPackageName = "com.roadmate.shop"
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }

    private fun inviteFriends(){
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Roadmate")
        val shareMessage = "\nDownload Roadmate from play store\n\n"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareMessage + "https://play.google.com/store/apps/details?id=com.roadmate.app")
        startActivity(Intent.createChooser(sharingIntent, "Share with"))
    }

    private fun closeKeyBoard(view: View){
        try {
            val imm: InputMethodManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        enableEditView(false)
        populateAccountDetails()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getMyVehiclesList()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity"
                duration = Toast.LENGTH_SHORT
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.editButton -> {
                enableEditView(true)
            }
            R.id.cancelEditButton -> {
                enableEditView(false)
            }
            R.id.addvehicle ->{
                activity?.startActivity<AddCustomerVehicleActivity>()
            }
            R.id.saveButton ->{
                (txtusername.text.toString().trim().isNotEmpty() && txtnumber.text.toString().trim().isNotEmpty()) .doIfTrue {
                    closeKeyBoard(txtusername)
                    closeKeyBoard(txtnumber)
                    editUser()
                }elseDo {
                    activity!!.toast {
                        message = "Name & Mobile number cannot be blank!"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
            R.id.logoutButton ->{
                AppSession.clearUserSession(activity!!)
                activity?.startActivity<LoginActivity>()
                activity!!.finish()
            }
            R.id.transaction ->{
                activity!!.startActivity<CustomerTransactionHistoryActivity>()
            }
            R.id.addMyShop ->{
                redirectToShopAppPlayStore()
            }
            R.id.inviteFriends ->{
                inviteFriends()
            }
            R.id.btnBookingHistory ->{
                activity!!.startActivity<BookedHistoryActivity>()
            }
        }
    }
}
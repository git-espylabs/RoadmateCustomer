package com.roadmate.app.ui.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.adapter.AvailableTimeSlotsAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.utils.CommonUtils
import com.roadmate.app.utils.NetworkUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_booked_history.*
import kotlinx.android.synthetic.main.fragment_shop_booking.*
import kotlinx.android.synthetic.main.fragment_shop_booking.progressLay
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ShopBookingFragment: Fragment() {
    
    lateinit var timeSlotsAdapter: AvailableTimeSlotsAdapter
    private var selectedTimeSlot = ""
    private var bookingID = ""
    private var bookingType = "3"


    private fun getBookingDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopBookingDetailsMaster>> {
                getShopBookingDetails(bookingDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateShopDetails(response.body()?.data!![0])
            }
            showProgress(false)
        }
    }

    private fun getAvailableShopTimeSlots(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<AvailableTimeSlotsMaster>> {
                getAvailableShopTimings(timeSlotDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                lay4.visibility = View.VISIBLE
                populateTimings(response.body()?.data!!)
            }else{
                lay4.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun sendSelectedShopTimings(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertTimeSlot(insertTimeSlotRequest())
            }
            if (response.isSuccessful && response.body()?.message != null && response.body()?.message!!.isNotEmpty()){
                activity!!.toast {
                    message = response.body()?.message!!
                    duration = Toast.LENGTH_SHORT
                }
                activity!!.finish();
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun populateShopDetails(data: ShopBookingDetailsTrans){
        tvAddress.text = data.address
        tvOpenTime.text = data.open_time
        tvClosingTime.text = data.close_time
        AppSession.selectedShopLocation = data.lattitude + "," + data.logitude
        AppSession.selectedShopPhone = data.phone_number

        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.image).into(shimage)

//        bookingID = data.id
    }

    private fun populateTimings(list: ArrayList<AvailableTimeSlotsTrans>){
        timeSlotList.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        timeSlotsAdapter = AvailableTimeSlotsAdapter(activity!!, list){obj, isSelected ->
            for (item in list){
                if (item.id != obj!!.id){
                    item.isSelected = false
                }
            }
            timeSlotsAdapter.notifyDataSetChanged()
            isSelected.doIfTrue {
                selectedTimeSlot = obj!!.name
            }elseDo {
                selectedTimeSlot = ""
            }
        }
        timeSlotList.adapter = timeSlotsAdapter
    }

    private fun bookingDetailsRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", arguments!!["shopid"])
            json.put("catid", arguments!!["catid"])
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun timeSlotDetailsRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", arguments!!["shopid"])
            json.put("bookdate", seldate.text.toString().trim())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun insertTimeSlotRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", arguments!!["shopid"])
            json.put("timeslot", selectedTimeSlot)
            json.put("book_type", bookingType)
            json.put("customer_id", UserDetails().userId)
            json.put("bookdate", seldate.text.toString().trim())
            json.put("book_id", bookingID)
            json.put("shop_category_id", arguments!!["catid"])
            json.put("model_id", DefaultVehicleDetails().vehicleModelId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun openDatePicker(){
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR);
        var mMonth = c.get(Calendar.MONTH);
        var mDay = c.get(Calendar.DAY_OF_MONTH);
        val datePickerDialog = DatePickerDialog(
            activity!!,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                var dates = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year
                seldate.text = CommonUtils.formatDate_ddMMyyyy(dates)
                getAvailableShopTimeSlots()
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun showProgress(show: Boolean){
        if (show){
            progressLay.visibility = View.VISIBLE
        }else{
            progressLay.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_booking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bookingType = arguments!!["booktype"].toString()
        bookingID = arguments!!["bookid"].toString()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getBookingDetails()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }

        confirmButton.setOnClickListener {
            if (selectedTimeSlot.isNotEmpty() && seldate.text.toString().trim().isNotEmpty()) {
                sendSelectedShopTimings()
            } else {
                activity!!.toast {
                    message = "Please select a time slot"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }

        seldate.setOnClickListener {
            openDatePicker()
        }
    }
}
package com.roadmate.app.ui.fragments

import android.app.DatePickerDialog
import android.content.Intent
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
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.AnswerQuestionActivity
import com.roadmate.app.ui.activities.ShopDetailsActivity
import com.roadmate.app.utils.CommonUtils
import com.roadmate.app.utils.NetworkUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_offer.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*


class OfferDetailsFragment: Fragment() {
    lateinit var timeSlotsAdapter: AvailableTimeSlotsAdapter
    private var selectedTimeSlot = ""
    private var bookingID = ""
    private var bookingType = "2"


    private fun getOfferDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<OfferDetailsMaster>> {
                getOfferDetails(offerDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateOfferDetails(response.body()?.data!![0])
            }
            showProgress(false)
        }
    }

    private fun getAvailableShopTimeSlots(){
        showProgress(true)
        try {
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<AvailableTimeSlotsMaster>> {
                    getAvailableShopTimings(timeSlotDetailsRequest())
                }
                if (response.isSuccessful && response.body()!!.data!! != null && response.body()!!.data!!.isNotEmpty()){
                    lay4.visibility = View.VISIBLE
                    populateTimings(response.body()?.data!!)
                }else{
                    lay4.visibility = View.GONE
                }
                showProgress(false)
            }
        } catch (e: Exception) {
            showProgress(false)
        }
    }

    private fun sendSelectedShopTimings(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertTimeSlot(insertOfferRequest())
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

    private fun populateOfferDetails(data: OfferDetailsTrans){
        title.text = data.title
        ofDesc.text = data.small_desc
        shname.text = data.shop_name
        shdis.text = data.shop_distance + " Km"
        if (data.offer_discount_type == "1") {
            item_discount_perc.visibility = View.GONE
            item_price.visibility = View.VISIBLE
            item_strikeprice.visibility = View.VISIBLE
            item_price.text = getString(R.string.Rs) + data.offer_amount
            item_strikeprice.text = getString(R.string.Rs) + data.normal_amount
        } else {
            item_discount_perc.visibility = View.VISIBLE
            item_price.visibility = View.VISIBLE
            item_strikeprice.visibility = View.GONE
            item_discount_perc.text = data.discount_percentage + "% " + "Discount"
        }
        endDate.text = CommonUtils.formatDate_yyyyMMdd(data.offer_end_date)
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.pic).into(offerImage)
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

    private fun offerDetailsRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("offerid", arguments!!["offerid"])
            json.put("shopid", arguments!!["shopid"])
            json.put("lat", AppSession.appLatitude)
            json.put("long",AppSession.appLongitude)
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
            json.put("bookdate", dateSelector.text.toString().trim())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun insertOfferRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", arguments!!["shopid"])
            json.put("timeslot", selectedTimeSlot)
            json.put("book_type", "2")
            json.put("customer_id", UserDetails().userId)
            json.put("bookdate", dateSelector.text.toString().trim())
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
                dateSelector.text = CommonUtils.formatDate_ddMMyyyy(dates)
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
        return inflater.inflate(R.layout.fragment_details_offer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        bookingID = arguments!!["offerid"].toString()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getOfferDetails()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }

        confirmButton.setOnClickListener {
            if (selectedTimeSlot.isNotEmpty() && dateSelector.text.toString().trim().isNotEmpty()) {
                sendSelectedShopTimings()
            } else {
                activity!!.toast {
                    message = "Please select a time slot"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }

        dateSelector.setOnClickListener {
            openDatePicker()
        }

        viewShop.setOnClickListener {
            val intent = Intent(activity, ShopDetailsActivity::class.java)
            intent.putExtra("shopId",arguments!!["shopid"].toString())
            startActivity(intent)
        }
    }
}
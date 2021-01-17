package com.roadmate.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.datatransport.runtime.logging.Logging.i
import com.roadmate.app.R
import com.roadmate.app.adapter.MoreServicesAdapter
import com.roadmate.app.adapter.ShopsServiceBookingListAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.ShopBookingListMaster
import com.roadmate.app.api.response.ShopBookingListTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.ShopBookingActivity
import com.roadmate.app.ui.activities.ShopBookingListActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_boocking_choose_service.*
import kotlinx.android.synthetic.main.fragment_package_tab.*
import kotlinx.android.synthetic.main.fragment_shop_booking_list.*
import kotlinx.android.synthetic.main.fragment_shop_booking_list.empty_caution
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.Console

class ShopBookingListFragment: Fragment() {

    private fun getBookingList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ShopBookingListMaster>> {
                getShopBookingListOnSelectedCat(bookingListRequest())
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                bookingList.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun populateList(list: ArrayList<ShopBookingListTrans>){
        empty_caution.visibility = View.GONE
        bookingList.visibility = View.VISIBLE

        bookingList.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = ShopsServiceBookingListAdapter(activity!!, list){obj ->
            val intent = Intent(context, ShopBookingActivity::class.java)
            intent.putExtra("shopid", obj!!.shopId)
            intent.putExtra("booktype", "3")
            intent.putExtra("catid", arguments!!["catid"].toString())
            intent.putExtra("bookid", obj!!.shopserviceid)
            startActivity(intent)
        }
        bookingList.adapter = adapter
    }

    private fun bookingListRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("catid", arguments!!["catid"])
            json.put("user_id", UserDetails().userId)
            json.put("latitude", AppSession.appLatitude)
            json.put("longitude", AppSession.appLongitude)
            json.put("modelid", DefaultVehicleDetails().vehicleModelId)
            jsonData = json.toString()
            android.util.Log.i("Dasdsadasd  ",jsonData.toString())
        } catch (e: JSONException) {

            e.printStackTrace()
        }

        return jsonData.toRequestBody()
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
        return inflater.inflate(R.layout.fragment_shop_booking_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getBookingList()
        }elseDo {
            activity!!.toast {
                message = "No Internet Connectivity"
                duration = Toast.LENGTH_LONG
            }
        }
    }
}
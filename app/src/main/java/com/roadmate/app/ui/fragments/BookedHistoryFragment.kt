package com.roadmate.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.BookedHistoryListAdapter
import com.roadmate.app.adapter.ShopsServiceBookingListAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.BookedHistoryMaster
import com.roadmate.app.api.response.BookedHistoryTrans
import com.roadmate.app.api.response.ShopBookingListMaster
import com.roadmate.app.api.response.ShopBookingListTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.ui.activities.ShopBookingActivity
import com.roadmate.app.ui.activities.ShopDetailsActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_booked_history.*
import kotlinx.android.synthetic.main.fragment_booked_history.empty_caution
import kotlinx.android.synthetic.main.fragment_sell_here_tab.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class BookedHistoryFragment: Fragment(), AdapterView.OnItemSelectedListener {


    private fun populateBookingTypes(){
        val category = resources.getStringArray(R.array.book_type_category)
        val arrayAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_dropdown_item,
            category
        )
        bookTypeSpn.adapter = arrayAdapter
        bookTypeSpn.onItemSelectedListener = this
    }

    private fun getBookedList(bookType: String){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<BookedHistoryMaster>> {
                getBookedHistory(bookedListRequest(bookType))
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                rvhistory.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun populateList(list: ArrayList<BookedHistoryTrans>){
        empty_caution.visibility = View.GONE
        rvhistory.visibility = View.VISIBLE

        rvhistory.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val adapter = BookedHistoryListAdapter(activity!!, list){ obj ->
            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("shopId", obj!!.shop_id)
            startActivity(intent)
        }
        rvhistory.adapter = adapter
    }

    private fun bookedListRequest(bookType: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customerid", UserDetails().userId)
            json.put("bookedtype", bookType)
            jsonData = json.toString()
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
        return inflater.inflate(R.layout.fragment_booked_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateBookingTypes()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
        getBookedList("1")
    }elseDo {
        activity!!.toast {
            message = "No Internet Connectivity"
            duration = Toast.LENGTH_LONG
        }
    }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getBookedList((position + 1).toString())
        }elseDo {
            activity!!.toast {
                message = "No Internet Connectivity"
                duration = Toast.LENGTH_LONG
            }
        }
    }
}
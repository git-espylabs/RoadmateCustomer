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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.MoreServicesAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.MoreServicesMaster
import com.roadmate.app.api.response.MoreServicesTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.ui.activities.CustomerViewAllShopsActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_customer_more_services.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CustomerMoreSericesFragment: Fragment() {

    private fun getMoreServicesList(){
        showProgress(true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getMoreServices()
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateServicesList(response.body()?.data!!)
            }else{
                activity!!.toast {
                    message = "Services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            showProgress(false)
        }
    }

    private fun  populateServicesList(list: ArrayList<MoreServicesTrans>){
        recycler_view_more_services.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        val serviceAdapter = MoreServicesAdapter(activity!!, list, list.size){shopId, shopName ->

            val intent = Intent(context, CustomerViewAllShopsActivity::class.java)
            intent.putExtra("shopname", shopName)
            intent.putExtra("shoptype", shopId)
            startActivity(intent)
        }
        recycler_view_more_services.adapter = serviceAdapter
    }

    private fun moreServiceJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("user_id", UserDetails().userId)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_more_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getMoreServicesList()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
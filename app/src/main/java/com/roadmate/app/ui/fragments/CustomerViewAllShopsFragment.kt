package com.roadmate.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.roadmate.app.R
import com.roadmate.app.adapter.AllShopsAdapter
import com.roadmate.app.adapter.MoreServicesAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.MoreServicesMaster
import com.roadmate.app.api.response.MoreServicesTrans
import com.roadmate.app.api.response.NearShopsMaster
import com.roadmate.app.api.response.NearShopsTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.CustomerViewAllShopsActivity
import com.roadmate.app.ui.activities.ShopDetailsActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_customer_more_services.*
import kotlinx.android.synthetic.main.fragment_customer_view_all_shops.*
import kotlinx.android.synthetic.main.fragment_customer_view_all_shops.loadingFrame
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CustomerViewAllShopsFragment: Fragment(){

    private fun getAllShopsList(){
        showProgress(true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<NearShopsMaster>> {
                getAllShops(nearestShopsJsonRequest())
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateShopsList(response.body()?.data!!)
            }else{
                activity!!.toast {
                    message = "Services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            showProgress(false)
        }
    }

    private fun  populateShopsList(list: ArrayList<NearShopsTrans>){
        allshopsRecycler.layoutManager = LinearLayoutManager(context)
        val shopsAdapter = AllShopsAdapter(activity!!, list){ shopId ->

            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)
        }
        allshopsRecycler.adapter = shopsAdapter
    }

    private fun nearestShopsJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shcat", arguments!!.getString("shoptype"))
            json.put("lat", AppSession.appLatitude)
            json.put("long", AppSession.appLongitude)
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
        return inflater.inflate(R.layout.fragment_customer_view_all_shops, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getAllShopsList()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
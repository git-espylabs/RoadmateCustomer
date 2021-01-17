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
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.AllShopsAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.NearShopsMaster
import com.roadmate.app.api.response.NearShopsTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.ShopDetailsActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_mechanics_tab.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class MechanicsTabFragment: Fragment() {

    private fun getNearestShops(){
        showProgress(true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<NearShopsMaster>> {
                getNearestMechanicsShops(createJsonRequest())
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateNearestShops(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun getAllShops(){
        showProgress(true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<NearShopsMaster>> {
                getAllMechanicsShops(createJsonRequest())
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateAllShops(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun createJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("user_id", UserDetails().userId)
            json.put("model_id", DefaultVehicleDetails().vehicleModelId)
            json.put("latitude", AppSession.appLatitude)
            json.put("longitude", AppSession.appLongitude)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }
    private fun populateNearestShops(list: ArrayList<NearShopsTrans>){
        nearby_mechanic_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        val shopsAdapter = AllShopsAdapter(activity!!, list){ shopId ->

            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)
        }
        nearby_mechanic_recycler_view.adapter = shopsAdapter
    }
    private fun populateAllShops(list: ArrayList<NearShopsTrans>){
        mechanic_recycler_view.layoutManager = LinearLayoutManager(context)
        val shopsAdapter = AllShopsAdapter(activity!!, list){ shopId ->

            val intent = Intent(context, ShopDetailsActivity::class.java)
            intent.putExtra("shopId", shopId)
            startActivity(intent)
        }
        mechanic_recycler_view.adapter = shopsAdapter
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
        return inflater.inflate(R.layout.fragment_mechanics_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnViewALl.setOnClickListener {
            getAllShops()
        }
    }

    override fun onResume() {
        super.onResume()
        if (DefaultVehicleDetails().vehicleId.isNotEmpty()){
            NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                empty_caution.visibility = View.GONE
                nearestLayout.visibility = View.VISIBLE
                allLayout.visibility = View.VISIBLE
                getNearestShops()
            }elseDo {
                activity!!.toast {
                    message = "No Internet connectivity!"
                }
                empty_caution.visibility = View.VISIBLE
                nearestLayout.visibility = View.GONE
                allLayout.visibility = View.GONE
            }
        }else{
            activity!!.toast {
                message = "Add your vehicle and explore now"
            }
            empty_caution.visibility = View.VISIBLE
            nearestLayout.visibility = View.GONE
            allLayout.visibility = View.GONE
        }
    }
}
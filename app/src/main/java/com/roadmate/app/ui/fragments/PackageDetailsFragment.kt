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
import com.bumptech.glide.Glide
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.adapter.PackageFeaturesAdapter
import com.roadmate.app.adapter.ShopsInPackageAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.ShopBookingActivity
import kotlinx.android.synthetic.main.fragment_pakage_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class PackageDetailsFragment: Fragment(){

    var packageId = ""
    private var shopsAdapter: ShopsInPackageAdapter? = null

    private fun getPackageDetails(){
        showProgress(true)
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<PackageMaster>> {
                getPackageDetails(packageDetailsRequestJSON("servicepackageid"))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populatePackagesDetails(response.body()?.data!![0])
            }else{
                activity!!.toast {
                    message = "Details not available at this moment!"
                    duration = Toast.LENGTH_SHORT
                }
            }
            getPackageShopsList()
        }
    }

    private fun getPackageShopsList(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<PackageShopsMaster>> {
                getPackageShops(packageShopsRequestJSON())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populatePackageShopsList(response.body()?.data!!)
            }
            getPackageServicesList()
        }
    }

    private fun getPackageServicesList(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<PackageFeaturesMaster>> {
                getPackageFeatures(packageDetailsRequestJSON("packageid"))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                packagedetails.visibility = View.VISIBLE
                featureRecycler.visibility = View.VISIBLE
                populatePackageFeatures(response.body()?.data!!)
            }else{
                packagedetails.visibility = View.GONE
                featureRecycler.visibility = View.GONE
            }
            showProgress(false)
        }
    }

    private fun booKPackage(shopid: String){
        shopsAdapter?.notifyDataSetChanged()
        val intent = Intent(context, ShopBookingActivity::class.java)
        intent.putExtra("shopid", shopid)
        intent.putExtra("booktype", "1")
        intent.putExtra("catid", "0")
        intent.putExtra("bookid", arguments!!["packageId"].toString())
        startActivity(intent)
    }

    private fun populatePackagesDetails(data: PackageTrans){
        Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + data.packageImage).into(package_image)
        package_name.text = data.packageName
        package_details.text = data.packageDescription
        item_price.text = resources.getString(R.string.Rs) + data.packageOfferPrice
        item_strikeprice.text = resources.getString(R.string.Rs) + data.packagePrice
    }

    private fun populatePackageShopsList(list: ArrayList<PackageShopsTrans>){
        shopRecycler.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.HORIZONTAL,
            false
        )
         shopsAdapter = ShopsInPackageAdapter(activity!!, list){shopId ->
             DefaultVehicleDetails().vehicleId.isNotEmpty().doIfTrue{
                 booKPackage(shopId!!)
             }elseDo {
                 activity!!.toast {
                     message = "Please add a vehicle and try again"
                     duration = Toast.LENGTH_SHORT
                 }
             }
        }
        shopRecycler.adapter = shopsAdapter
    }

    private fun populatePackageFeatures(list: ArrayList<PackageFeaturesTrans>){
        featureRecycler.layoutManager = LinearLayoutManager(
            activity,
            RecyclerView.VERTICAL,
            false
        )
        list.forEach {
            if (null == it.feature || it.feature.isNullOrBlank()){
                list.remove(it)
            }
        }
        val  featuresAdapter = PackageFeaturesAdapter(activity!!, list)
        featureRecycler.adapter = featuresAdapter
    }

    private fun packageDetailsRequestJSON(param: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, packageId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun packageShopsRequestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("packageid", packageId)
            json.put("lat", AppSession.appLatitude)
            json.put("long", AppSession.appLongitude)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun packageBookingRequestJSON(shopid: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopid", shopid)
            json.put("customerid", UserDetails().userId)
            json.put("packageid", packageId)
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
        return inflater.inflate(R.layout.fragment_pakage_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        packageId = arguments!!.getString("packageId")!!
        getPackageDetails()
    }
}
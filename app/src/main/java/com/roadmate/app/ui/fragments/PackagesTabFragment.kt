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
import com.roadmate.app.adapter.OfferListAdapter
import com.roadmate.app.adapter.PackageAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.CustomerOfferMaster
import com.roadmate.app.api.response.CustomerOfferTrans
import com.roadmate.app.api.response.PackageMaster
import com.roadmate.app.api.response.PackageTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.ui.activities.OfferDetailsActivity
import com.roadmate.app.ui.activities.PackageDetailsActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_package_tab.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.ArrayList

class PackagesTabFragment: Fragment() {


    private fun getPackagesList(){
        showProgress(true)
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<PackageMaster>> {
                getPackages(createRequestJson("user_id", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populatePackagesList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                offerRecycler.visibility = View.GONE
                packageRecycler.visibility = View.GONE
                indicator1.visibility = View.GONE

                packageRecycler.visibility = View.GONE
                indicator2.visibility = View.VISIBLE
            }
            showProgress(false)
        }
    }

    private fun populatePackagesList(packageList: ArrayList<PackageTrans>){
        packageRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var packageAdapter = PackageAdapter(activity!!, packageList){packageId ->
            val intent = Intent(context, PackageDetailsActivity::class.java)
            intent.putExtra("PackageId", packageId)
            startActivity(intent)
        }
        packageRecycler.adapter = packageAdapter
    }

    private fun getOffersList(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<CustomerOfferMaster>> {
                getCustomerOffers(createRequestJson("user_id", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateOffersList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                offerRecycler.visibility = View.GONE
                packageRecycler.visibility = View.GONE
                indicator1.visibility = View.VISIBLE

                packageRecycler.visibility = View.GONE
                indicator2.visibility = View.GONE
            }
        }
    }

    private fun populateOffersList(list: ArrayList<CustomerOfferTrans>){
        offerRecycler.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        var offerAdapter = OfferListAdapter(activity!!, list){ obj ->
            val intent = Intent(context, OfferDetailsActivity::class.java)
            intent.putExtra("shopid", obj!!.shop_id)
            intent.putExtra("offerid", obj!!.id)
            intent.putExtra("catid", obj!!.shop_cat_id)
            startActivity(intent)
        }
        offerRecycler.adapter = offerAdapter
    }

    private fun createRequestJson(param: String, value: String): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("user_id", UserDetails().userId)
            json.put("model_id", DefaultVehicleDetails().vehicleModelId)
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

    private fun invokeOfferList(){
        NetworkUtils.isNetworkConnected(activity).doIfTrue {
            getOffersList()
            empty_caution.visibility = View.GONE
            offerRecycler.visibility = View.VISIBLE
            indicator1.visibility = View.VISIBLE

            packageRecycler.visibility = View.GONE
            indicator2.visibility = View.GONE
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
            empty_caution.visibility = View.VISIBLE
            offerRecycler.visibility = View.GONE
            packageRecycler.visibility = View.GONE
            indicator1.visibility = View.VISIBLE

            packageRecycler.visibility = View.GONE
            indicator2.visibility = View.GONE
        }
    }

    private fun invokePackageList(){
        NetworkUtils.isNetworkConnected(activity).doIfTrue {
            getPackagesList()
            empty_caution.visibility = View.GONE
            offerRecycler.visibility = View.GONE
            indicator1.visibility = View.GONE

            packageRecycler.visibility = View.VISIBLE
            indicator2.visibility = View.VISIBLE
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
            empty_caution.visibility = View.VISIBLE
            offerRecycler.visibility = View.GONE
            packageRecycler.visibility = View.GONE
            indicator1.visibility = View.GONE

            indicator2.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_package_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        invokeOfferList()
        offersTv.setOnClickListener {
            invokeOfferList()
        }

        packagesTv.setOnClickListener {
            invokePackageList()
        }
    }
}
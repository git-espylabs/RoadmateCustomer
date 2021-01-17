package com.roadmate.app.ui.fragments

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
import com.roadmate.app.adapter.SimpleServicesListAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_shop_details.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.ArrayList

class ShopDetailsFragment: Fragment(), View.OnClickListener{

    var shopRating: Float = 0.0F

    private fun setListeners(){
        viewImages.setOnClickListener(this)
        rate_now.setOnClickListener(this)
        edit_review.setOnClickListener(this)
        postreview.setOnClickListener(this)
        savereview.setOnClickListener(this)
    }

    private fun getSelectedShopDetails(){
        showProgress(true)
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<ShopDataMaster>> {
                getShopDetails(shopDataJsonRequest("shopid", arguments!!.getString("shopId")!!))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateViews(response.body()?.data!![0])
            }

            if (!response.isSuccessful || response.body()?.data!!.isEmpty()){
                activity!!.toast {
                    message = "Shop details not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            getOtherShopServices()
        }
    }

    private fun getOtherShopServices(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getShopServices(shopDataJsonRequest("shop", arguments!!.getString("shopId")!!))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateServicesList(response.body()?.data!!)
            }

            if (!response.isSuccessful || response.body()?.data!!.isEmpty()){
                activity!!.toast {
                    message = "Some services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            getShopReviews()
        }
    }

    private fun getShopReviews(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ReviewMaster>> {
                getShopReviews(shopDataJsonRequest("shop", arguments!!.getString("shopId")!!))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateReviewViews(response.body()?.data!![0])
            }else{
                manageReviewLayouts(false)
            }
            showProgress(false)
        }
    }

    private fun postReviews(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                postShopReviews(reviewPostJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message!! == "Success"){
                manageReviewLayouts(false)
                activity!!.toast {
                    message = "Review added"
                    duration = Toast.LENGTH_SHORT
                }
            }
            getShopReviews()
        }
    }


    private fun populateViews(data: ShopDataTrans){
        Glide.with(activity!!).load(BuildConfig.BANNER_URL_ENDPOINT + data.shopImage).into(viewImages)
        shop_name.text = data.shopName
        shop_address.text = data.shopAddress
        shop_time.text = data.shopTiming
        AppSession.selectedShopLocation = data.shopLatitude + "," + data.shopLongitude
        AppSession.selectedShopPhone = data.shopPhone
        shop_number.text = data.shopPhone
        shop_description.text = data.description
    }

    private fun populateServicesList(serviceList: ArrayList<MoreServicesTrans>){
        shoptypeRecycler.layoutManager = LinearLayoutManager(context)
        val serviceAdapter = SimpleServicesListAdapter(activity!!,serviceList)
        shoptypeRecycler.adapter = serviceAdapter
    }

    private fun populateReviewViews(data: ReviewTrans){
        data.reviewCount.isNotEmpty().doIfTrue {
            shopRating = data.reviewCount.toFloat()
            manageReviewLayouts(false)
        }
    }

    private fun manageReviewLayouts(isEdit: Boolean){
        isEdit.doIfTrue {
            rate_now.visibility = View.GONE
            edit_review.visibility = View.GONE

            ratingBar.visibility = View.VISIBLE
            addTitle.visibility = View.VISIBLE
            postreview.visibility = View.VISIBLE
            rate_layout.visibility = View.VISIBLE
            if (shopRating > 0.0F){
                ratingBar2.rating = shopRating
            }
        }elseDo {
            if (shopRating > 0.0F) {
                ratingBar2.rating = shopRating
                rate_now.visibility = View.GONE
                edit_review.visibility = View.VISIBLE
            }else{
                rate_now.visibility = View.VISIBLE
                edit_review.visibility = View.GONE
            }
            ratingBar.visibility = View.GONE
            rate_layout.visibility = View.GONE
            postreview.visibility = View.GONE
        }
    }

    private fun shopDataJsonRequest(parameter: String, value: String) : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(parameter, value)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun reviewPostJsonRequest() : RequestBody {

        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("userid", UserDetails().userId)
            json.put("shop", arguments!!.getString("shopId"))
            json.put("review", ""+ratingBar.rating)
            json.put("comment", addTitle.text.toString())
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
        return inflater.inflate(R.layout.fragment_shop_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getSelectedShopDetails()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity"
                duration = Toast.LENGTH_SHORT
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.viewImages -> {

            }
            R.id.rate_now -> {
                manageReviewLayouts(true)
            }
            R.id.edit_review -> {
                manageReviewLayouts(true)
            }
            R.id.postreview -> {
                NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                    postReviews()
                }elseDo {
                    activity!!.toast {
                        message = "No Internet connectivity"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
            R.id.savereview -> {

            }
        }
    }
}
package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.OfferDetailsMaster
import com.roadmate.app.api.response.OfferDetailsTrans
import com.roadmate.app.api.response.ProductDetailsMaster
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.utils.CommonUtils
import com.roadmate.app.utils.NetworkUtils
import com.roadmate.shop.api.response.ProductOfferMaster
import com.roadmate.shop.api.response.ProductOfferTrans
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details_offer.*
import kotlinx.android.synthetic.main.fragment_product_offer_details.*
import kotlinx.android.synthetic.main.fragment_product_offer_details.endDate
import kotlinx.android.synthetic.main.fragment_product_offer_details.item_price
import kotlinx.android.synthetic.main.fragment_product_offer_details.item_strikeprice
import kotlinx.android.synthetic.main.fragment_product_offer_details.ofDesc
import kotlinx.android.synthetic.main.fragment_product_offer_details.offerImage
import kotlinx.android.synthetic.main.fragment_product_offer_details.progressLay
import kotlinx.android.synthetic.main.fragment_product_offer_details.title
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class ProductOfferDetailsFragment: BaseFragment() {


    private fun getOfferDetails(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<ProductOfferMaster>> {
                getProductOfferDetails(offerDetailsRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateOfferDetails(response.body()?.data!![0])
            }
            showProgress(false)
        }
    }

    private fun populateOfferDetails(data: ProductOfferTrans){
        title.text = data.title
        ofDesc.text = data.description
        item_price.text = getString(R.string.Rs) + data.normal_amount
        item_strikeprice.text = getString(R.string.Rs) + data.discount_amount
        endDate.text = CommonUtils.formatDate_yyyyMMdd(data.end_date)
        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.product_picture).into(offerImage)
    }

    private fun offerDetailsRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("id", arguments!!["offerid"])
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
        return inflater.inflate(R.layout.fragment_product_offer_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getOfferDetails()
        }elseDo {
            activity!!.toast {
                message = "No Internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
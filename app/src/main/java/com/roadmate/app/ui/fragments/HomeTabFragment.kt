package com.roadmate.app.ui.fragments

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.roadmate.app.R
import com.roadmate.app.adapter.BannerAdapter
import com.roadmate.app.adapter.OfferListAdapter
import com.roadmate.app.adapter.PackageAdapter
import com.roadmate.app.adapter.RatedShopsAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_FOUR_WHEELER
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_THREE_WHEELER
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_TWO_WHEELER
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.location.AppLocation
import com.roadmate.app.location.AppLocationListener
import com.roadmate.app.log.AppLogger
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.*
import com.roadmate.app.utils.NetworkUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home_tab.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import java.util.*

class HomeTabFragment: Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AppLocationListener {

    var bannerHandler = Handler()
    var bannerSwipeTimer = Timer()
    private var currentPage = 0
    private var NUM_PAGES = 0
    private val SHOWCASE_ID = "tooltip"
    private val SHOWCASE_DELAY = 3000
    private var isBannerTimerRunning = false


    private fun presentShowcaseView() {
        MaterialShowcaseView.Builder(activity)
            .setTarget(addvehicle)
            .setContentText("Add your vehicle to see mechanic shops and available packages :)")
            .setDismissText("GOT IT")
            .setDismissOnTouch(true)
            .setContentTextColor(ContextCompat.getColor(activity!!, R.color.white))
            .setMaskColour(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark))
            .setDelay(SHOWCASE_DELAY)
            .singleUse(SHOWCASE_ID)
            .show()
    }

    private fun setListeners(){
        addvehicle.setOnClickListener(this)
        post_query.setOnClickListener(this)
        img_info.setOnClickListener(this)
        mechanic_category.setOnClickListener(this)
        spareparts_category.setOnClickListener(this)
        waterService.setOnClickListener(this)
        roadsideService.setOnClickListener(this)
        view_wallet.setOnClickListener(this)
        swipelayout.setOnRefreshListener(this)
        defaultVehicle.setOnClickListener(this)
        btnBookService.setOnClickListener(this)
        payPending.setOnClickListener(this)
    }

    private fun processAppBanner(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<AppBannerMaster>> {
                getAppBanners()
            }
            if (response.isSuccessful && response.body()?.message =="Success"){
                AppSession.appBannerList = response.body()?.data!!
                populateAppBanner(response.body()?.data!!)
            }
            getWalletCreditUpdate()
        }
    }

    private fun populateAppBanner(bannerList: ArrayList<AppBannerTrans>){
        NUM_PAGES = bannerList.size
        val viewPagerAdapter = BannerAdapter(activity!!, bannerList)
        home_view_pager.adapter = viewPagerAdapter

        bannerSwipeTimer.schedule(object : TimerTask() {
            override fun run() {
                isBannerTimerRunning = true
                bannerHandler.post {
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0
                    }
                    home_view_pager.setCurrentItem(currentPage++, true)
                }
            }
        }, 0, 6000)

    }

    private fun showCustomerDefaultVehicle(){
        DefaultVehicleDetails().vehicleId.isNotEmpty().doIfTrue {
            defaultVehicle.visibility = View.VISIBLE
            addvehicle.visibility = View.GONE

            v_number.text = DefaultVehicleDetails().vehicleNo
            v_model.text = DefaultVehicleDetails().vehicleModel
            when{
                DefaultVehicleDetails().vehicleType.equals(VEHICLE_TYPE_TWO_WHEELER, true) -> Glide.with(activity!!).load(R.drawable.two_wheeler).into(v_type)
                DefaultVehicleDetails().vehicleType.equals(VEHICLE_TYPE_FOUR_WHEELER, true) -> Glide.with(activity!!).load(R.drawable.four_wheeler).into(v_type)
                DefaultVehicleDetails().vehicleType.equals(VEHICLE_TYPE_THREE_WHEELER, true) -> Glide.with(activity!!).load(R.drawable.three_wheeler).into(v_type)
                else -> Picasso.with(context).load(R.drawable.heavy_vehicle).into(v_type)
            }
        }elseDo {
            presentShowcaseView()
            defaultVehicle.visibility = View.GONE
            addvehicle.visibility = View.VISIBLE
        }
    }

    private fun getWalletCreditUpdate(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<WalletMaster>> {
                getWalletDetails(createWalletRequestJson("userid", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.balance!!.isNotEmpty()){
                txtwalletbalance.text = getString(R.string.Rs) + response.body()?.balance!!
                try {
                    AppSession.myWalletBalance = response.body()?.balance!!.toInt()
                } catch (e: Exception) {
                }
            }else{
                txtwalletbalance.text = getString(R.string.Rs) + "0"
            }
            getPackagesList()
        }
    }

    private fun postCustomerQuery(){
        editText.text.toString().trim().isNotEmpty().doIfTrue {
            lifecycleScope.launch {
                progress.visibility = View.VISIBLE
                send.visibility = View.GONE
                val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                    insertCustomerQuery(postQueryRequestJson(editText.text.toString().trim()))
                }
                progress.visibility = View.GONE
                send.visibility = View.VISIBLE
                editText.setText("")
            }
        }
    }

    private fun createWalletRequestJson(param: String, value: String): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, value)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun createRequestJson(param: String, value: String): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put(param, value)
            json.put("model_id", DefaultVehicleDetails().vehicleModelId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun postQueryRequestJson(value: String): RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("Post_querys", value)
            json.put("userid", UserDetails().userId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showWalletInfo(){
        val dialogBuilder =
            AlertDialog.Builder(context!!)
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(R.layout.wallet_info, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.setCancelable(true)
        val editText =
            dialogView.findViewById<Button>(R.id.button)
        editText.setOnClickListener { alertDialog.cancel() }
        alertDialog.show()
    }

    private fun getPackagesList(){
        if (DefaultVehicleDetails().vehicleId.isNotEmpty()) {
            lifecycleScope.launch{
                val response = APIManager.call<ApiServices, Response<PackageMaster>> {
                    getPackages(createRequestJson("user_id", UserDetails().userId))
                }
                if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                    packageLinear.visibility = View.VISIBLE
                    populatePackagesList(response.body()?.data!!)
                }else{
                    packageLinear.visibility = View.GONE
                }
                getOffersList()
            }
        }
    }

    private fun getOffersList(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<CustomerOfferMaster>> {
                getCustomerOffers(createRequestJson("user_id", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                offerLinear.visibility = View.VISIBLE
                populateOffersList(response.body()?.data!!)
            }else{
                offerLinear.visibility = View.GONE
            }
            checkForPendingPayments()
        }
    }

    private fun checkForPendingPayments(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                getUserPendingPaymentsCount(pendingPaymentsRequestJSON())
            }
            if (response.isSuccessful && !response.body()?.data!!.isNullOrEmpty() && response.body()?.data!!.toInt() > 0){
                paymentPendingLay.visibility = View.VISIBLE
            }else{
                paymentPendingLay.visibility = View.GONE
            }


        }
    }



    private fun pendingPaymentsRequestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("userid", UserDetails().userId)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun getRatedShopsList(){
        lifecycleScope.launch{
            val response = APIManager.call<ApiServices, Response<RatedShopsMaster>> {
                getRatedMechanicShops(createRequestJson("", ""))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateRatedShopsList(response.body()?.data!!)
                mechanicLinear.visibility = View.VISIBLE
            }else{
                mechanicLinear.visibility = View.GONE
            }
        }
    }

    private fun populateRatedShopsList(shopList: ArrayList<RatedShopsTrans>){
        top_rated_mechanic_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        var shopsAdapter = RatedShopsAdapter(activity!!, shopList){ shopId ->
            val intent = Intent(context, PackageDetailsActivity::class.java)
            intent.putExtra("PackageId", shopId)
            startActivity(intent)
        }
        top_rated_mechanic_recycler_view.adapter = shopsAdapter
    }

    private fun populatePackagesList(packageList: ArrayList<PackageTrans>){
        packages_recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        var packageAdapter = PackageAdapter(activity!!, packageList){packageId ->
            val intent = Intent(context, PackageDetailsActivity::class.java)
            intent.putExtra("PackageId", packageId)
            startActivity(intent)
        }
        packages_recycler_view.adapter = packageAdapter
    }

    private fun populateOffersList(list: ArrayList<CustomerOfferTrans>){
        rvOffers.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        var offerAdapter = OfferListAdapter(activity!!, list){ obj ->
            DefaultVehicleDetails().vehicleId.isNotEmpty().doIfTrue{
                val intent = Intent(context, OfferDetailsActivity::class.java)
                intent.putExtra("shopid", obj!!.shop_id)
                intent.putExtra("offerid", obj!!.id)
                intent.putExtra("catid", obj!!.shop_cat_id)
                startActivity(intent)
            }elseDo {
                activity!!.toast {
                    message = "Please add a vehicle and try again"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }
        rvOffers.adapter = offerAdapter
    }

    private fun moveToAllShopsList(shopType: String, shopTypeId: String){
        val intent = Intent(context, CustomerViewAllShopsActivity::class.java)
        intent.putExtra("shopname", shopType)
        intent.putExtra("shoptype", shopTypeId)
        startActivity(intent)
    }

    private fun getLocation(){
        AppLocation().getAppLocation(this, context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getLocation()
        setListeners()
        processAppBanner()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.post_query -> {
                NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                    postCustomerQuery()
                } elseDo {
                    activity!!.toast {
                        message = "No Internet connectivity!"
                        duration = Toast.LENGTH_LONG
                    }
                }
            }

            R.id.addvehicle ->{
                activity?.startActivity<AddCustomerVehicleActivity>()
            }

            R.id.img_info -> {
                showWalletInfo()
            }

            R.id.mechanic_category ->{
                moveToAllShopsList("Mechanics", "1")
            }

            R.id.spareparts_category ->{
                moveToAllShopsList("Spares", "2")
            }

            R.id.waterService ->{
                moveToAllShopsList("Water Service", "4")
            }

            R.id.roadsideService ->{
                moveToAllShopsList("Assistance", "5")
            }

            R.id.view_wallet ->{
                activity!!.startActivity<CustomerTransactionHistoryActivity>()
            }

            R.id.defaultVehicle -> {
                VehicleListBottomSheet {
                    showCustomerDefaultVehicle()
                    DefaultVehicleDetails().vehicleId.isNotEmpty().doIfTrue{
                        getPackagesList()
                    }
                }.show(fragmentManager!!, "Dialog")
            }

            R.id.btnBookService ->{
                DefaultVehicleDetails().vehicleId.isNotEmpty().doIfTrue {
                    activity!!.startActivity<BookingChooseServiceActivity>()
                }elseDo {
                    activity!!.toast {
                        message = "Please add a vehicle and try again"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }

            R.id.payPending ->{
                activity!!.startActivity<PendingPaymentListActivity>()
            }
        }
    }

    override fun onRefresh() {
        getLocation()
        processAppBanner()
        showCustomerDefaultVehicle()
        swipelayout.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        bannerHandler.removeCallbacks(null)
        bannerSwipeTimer.cancel()
        isBannerTimerRunning = false
    }

    override fun onResume() {
        super.onResume()
        showCustomerDefaultVehicle()
        if (AppSession.appBannerList.isNotEmpty() && !isBannerTimerRunning){
            bannerHandler= Handler()
            bannerSwipeTimer = Timer()
            populateAppBanner(AppSession.appBannerList)
        }
    }

    override fun onLocationReceived(location: Location) {
        (location != null).doIfTrue {
            AppSession.appLatitude = ""+location.latitude
            AppSession.appLongitude = ""+location.longitude
        }elseDo {
            AppLogger.error("Location not received!")
        }
    }

    override fun onLocationSettingsSatisfied() {
    }
}
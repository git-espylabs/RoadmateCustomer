package com.roadmate.app.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.roadmate.app.R
import com.roadmate.app.adapter.TabAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfFalse
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.log.AppLogger
import com.roadmate.app.preference.DefaultVehicleDetails
import com.roadmate.app.preference.FcmDetails
import com.roadmate.app.preference.UserDetails
import kotlinx.android.synthetic.main.fragment_customer_home.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class CustomerHomeFragment: BaseFragment() {

    lateinit var tabAdapter: TabAdapter


    private fun registerForFcm(){
        if (FcmDetails().fcmToken != ""){
            FcmDetails().isFcmRegistered.doIfFalse {
                processFcmRegistration()
            }
        }else{
            requestFcmToke()
        }
    }

    private fun requestFcmToke(){
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener(OnCompleteListener {
            it.isSuccessful.doIfTrue {
                AppLogger.info("FCM Token", it.result!!.token)
                if (it.result!!.token != "") {
                    FcmDetails().fcmToken = it.result!!.token
                    processFcmRegistration()
                }
            }elseDo {
                AppLogger.info("FCM Token", "getInstanceId failed: " + it.exception)
            }
        })
    }

    private fun processFcmRegistration(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                registerForFcm(fcmRegistrationJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                FcmDetails().isFcmRegistered = true
                AppLogger.info("FCM Registered")
            }
        }
    }

    private fun fcmRegistrationJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("customerid", UserDetails().userId)
            json.put("fcmid", FcmDetails().fcmToken)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        registerForFcm()
        tabAdapter = TabAdapter(activity!!.supportFragmentManager)
        tabAdapter.addFragment(HomeTabFragment(), "HOME")
        tabAdapter.addFragment(MechanicsTabFragment(), "MECHANICS")
        tabAdapter.addFragment(PackagesTabFragment(), "OFFERS")
        viewPager.adapter = tabAdapter
        tabs.setupWithViewPager(viewPager)
    }

    override fun onResume() {
        super.onResume()

        when{
            DefaultVehicleDetails().vehicleId.isNullOrEmpty() ->{
                noVehicleWarning()
            }
        }
    }

    private fun noVehicleWarning(){
        AlertDialog.Builder(requireContext())
            .setMessage("Add your vehicle and explore now")
            .setPositiveButton("Close") { _, _ ->
            }
            .show()
    }
}
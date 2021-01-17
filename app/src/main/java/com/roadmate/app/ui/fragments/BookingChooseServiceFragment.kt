package com.roadmate.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.MoreServicesAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.MoreServicesMaster
import com.roadmate.app.api.response.MoreServicesTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.startActivity
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.ui.activities.*
import kotlinx.android.synthetic.main.fragment_boocking_choose_service.*
import kotlinx.android.synthetic.main.fragment_customer_more_services.*
import kotlinx.android.synthetic.main.fragment_customer_more_services.loadingFrame
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class BookingChooseServiceFragment: BaseFragment() {

    var servicesList: ArrayList<MoreServicesTrans> = arrayListOf()
    private lateinit var serviceAdapter: MoreServicesAdapter

    private fun getMoreServicesList(){
        showProgress(true)
        lifecycleScope.launch{
            val response =  APIManager.call<ApiServices, Response<MoreServicesMaster>> {
                getMoreServices()
            }

            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                servicesList = response.body()?.data!!
                var count: Int
                if (response.body()?.data!!.size>4){
                    count = 4
                    btnViewMore.visibility = View.VISIBLE
                }else{
                    btnViewMore.visibility = View.GONE
                    count = response.body()?.data!!.size
                }
                populateServicesList(response.body()?.data!!, count)
            }else{
                activity!!.toast {
                    message = "Services not available at this moment!"
                    duration = Toast.LENGTH_LONG }
            }
            showProgress(false)
        }
    }

    private fun  populateServicesList(list: ArrayList<MoreServicesTrans>, count: Int){
        rvServiceList.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        serviceAdapter = MoreServicesAdapter(activity!!, list, count){catid, _ ->
            val intent = Intent(context, ShopBookingListActivity::class.java)
            intent.putExtra("catid", catid)
            startActivity(intent)
        }
        rvServiceList.adapter = serviceAdapter
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
        return inflater.inflate(R.layout.fragment_boocking_choose_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        getMoreServicesList()

        btnViewMore.setOnClickListener {
            btnViewMore.visibility = View.GONE
            populateServicesList(servicesList, servicesList.size)
        }
        btnBookingHistory.setOnClickListener{
            activity?.startActivity<BookedHistoryActivity>()
        }
    }
}
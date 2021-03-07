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
import com.roadmate.app.R
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.NotificationMaster
import com.roadmate.app.api.response.NotificationTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.utils.NetworkUtils
import com.roadmate.app.adapter.NotificationListAdapter
import kotlinx.android.synthetic.main.fragment_notifocation.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class NotificationFragment: Fragment() {

    private fun getNotificationsList(){
        showProgress(true)
        try {
            lifecycleScope.launch {
                val response = APIManager.call<ApiServices, Response<NotificationMaster>> {
                    getUserNotifications(requestJSON())
                }
                if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                    populateList(response.body()?.data!!)
                }else{
                    empty_caution.visibility = View.VISIBLE
                    notification_recycler.visibility = View.GONE
                }
                showProgress(false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            showProgress(false)
        }
    }

    private fun populateList(list: ArrayList<NotificationTrans>){
        empty_caution.visibility = View.GONE
        notification_recycler.visibility = View.VISIBLE

        notification_recycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter =  NotificationListAdapter(activity!!, list){

        }
        notification_recycler.adapter = adapter
    }

    private fun requestJSON() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("usertypeid", "1")
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun showProgress(show:Boolean){
        if (show){
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
        return inflater.inflate(R.layout.fragment_notifocation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getNotificationsList()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            notification_recycler.visibility = View.GONE
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
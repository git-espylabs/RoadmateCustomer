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
import com.roadmate.app.adapter.TransactionHistoryAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.TransactionHistoryMaster
import com.roadmate.app.api.response.TransactionHistoryTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_customer_transaction_history.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CustomerTransactionsHistoryFragment: Fragment(){

    private fun getHistory(){
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<TransactionHistoryMaster>> {
                getWalletTransactionHistory(createRequestJson("", UserDetails().userId))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateList(response.body()?.data!!)
            }else{
                empty_caution.visibility = View.VISIBLE
                transaction_history.visibility = View.GONE
            }
        }
    }

    private fun populateList(list: ArrayList<TransactionHistoryTrans>){
        empty_caution.visibility = View.GONE
        transaction_history.visibility = View.VISIBLE

        transaction_history.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter = TransactionHistoryAdapter(activity!!, list){_ ->

        }
        transaction_history.adapter = adapter
    }

    private fun createRequestJson(param: String, value: String): RequestBody {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_transaction_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getHistory()
        }elseDo {
            empty_caution.visibility = View.VISIBLE
            transaction_history.visibility = View.GONE
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_LONG
            }
        }
    }
}
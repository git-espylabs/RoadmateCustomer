package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.PendingPaymentsAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.PaymentItemMaster
import com.roadmate.app.api.response.PaymentItemTrans
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.log.AppLogger
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.razorpay.PaymentUtils
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_pending_payments.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response


class PendingPaymentsFragment: Fragment() {

    private var paymentMode = "2"
    lateinit var listAdapter: PendingPaymentsAdapter
    private var userSelectionList: ArrayList<PaymentItemTrans> = arrayListOf()
    private var userPayableAmount = 0.0
    private var transactionId = ""

    private fun setListeners(){
        rgPayMode.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbOnline -> {
                    btnProceed.text = "PROCEED TO PAYMENT"
                    paymentMode = "2"
                }

                R.id.rbCash -> {
                    btnProceed.text = "SUBMIT"
                    paymentMode = "1"
                }
            }
        }

        btnProceed.setOnClickListener {
            if (userSelectionList.isNotEmpty() && userPayableAmount > 0){

                if (paymentMode == "2") {
                    PaymentUtils(activity!!, userPayableAmount).proceedPayment()
                } else {
                    updatePaymentStatus("-1")
                }
            }else if (userPayableAmount <= 0){
                activity!!.toast {
                    message = "Price not available"
                    duration = Toast.LENGTH_SHORT
                }
            }else{
                activity!!.toast {
                    message = "Please select at least one bill to pay"
                    duration = Toast.LENGTH_SHORT
                }
            }
        }
    }

    private fun getPaymentList(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<PaymentItemMaster>> {
                getPaymentList(paymentListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populatePaymentList(response.body()?.data!!)
            }else{
//                populatePaymentList(createTemproryList())
                showEmptyCaution(true)
            }
            showProgress(false)
        }
    }

    private fun populatePaymentList(list: ArrayList<PaymentItemTrans>){
        showEmptyCaution(false)
        userSelectionList.clear()

        var accountBalance = AppSession.myWalletBalance;
        for (item in list){
            if (!item.itemTotal.isNullOrEmpty() && item.itemTotal != "0"){
                item.isItemSelected = true
                if (item.itemTotal.toInt() > 500 && accountBalance>=50){
                    item.isRedeemed = true
                    accountBalance -= 50
                }
                userSelectionList.add(item)
            }
        }

        paymentList.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        listAdapter = PendingPaymentsAdapter(activity!!, list){obj ->
            for (listItem in userSelectionList){
                if (obj.itemId == listItem.itemId){
                    userSelectionList.remove(obj)
                    break
                }
            }
            if (obj!!.isItemSelected && !obj!!.itemTotal.isNullOrEmpty() && obj!!.itemTotal != "0"){
                userSelectionList.add(obj)
            }
//            setTotalPayableAmount(list)
        }

        paymentList.adapter = listAdapter;

        paymentList.viewTreeObserver
            .addOnGlobalLayoutListener(OnGlobalLayoutListener {
                //At this point the layout is complete and the
                //dimensions of recyclerView and any child views are known.
                setTotalPayableAmount(list)
            })
    }

    private fun setTotalPayableAmount(list: ArrayList<PaymentItemTrans>){
        var  total: Double = 0.00
        var totalRedeems = 0;
        for (item in list){
            if (!item.itemTotal.isNullOrEmpty() && item.itemTotal.toDouble() > 500 && item.isItemSelected && item.isRedeemed){
                total += (item.itemTotal.toDouble() - 50)
            }else if (!item.itemTotal.isNullOrEmpty() && item.itemTotal.toDouble() > 500 && item.isItemSelected && !item.isRedeemed){
                total += (item.itemTotal.toDouble())
            }else if (!item.itemTotal.isNullOrEmpty() && item.itemTotal.toDouble() < 500 && item.isItemSelected){
                total += (item.itemTotal.toDouble())
            }
        }
        userPayableAmount = total
        var totAmnt = String.format("%.2f", total)
        gTotal.text = activity!!.resources.getString(R.string.Rs) + totAmnt + "/-"
    }

    private fun paymentListJsonRequest() : RequestBody {
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

    private fun showProgress(show:Boolean){
        show.doIfTrue {
            loadingFrame.visibility = View.VISIBLE
        }elseDo {
            loadingFrame.visibility = View.GONE
        }
    }

    private fun showPaymentProgress(show:Boolean){
        show.doIfTrue {
            spin_kit.visibility = View.VISIBLE
            btnProceed.visibility = View.INVISIBLE
        }elseDo {
            spin_kit.visibility = View.GONE
            btnProceed.visibility = View.VISIBLE
        }

    }

    private fun showEmptyCaution(show:Boolean){
        show.doIfTrue {
            empty_caution.visibility = View.VISIBLE
            paymentList.visibility = View.GONE
            btnProceed.visibility = View.GONE
            gTotalLay.visibility = View.GONE
        }elseDo {
            empty_caution.visibility = View.GONE
            paymentList.visibility = View.VISIBLE
            btnProceed.visibility = View.VISIBLE
            gTotalLay.visibility = View.VISIBLE
        }
    }

    fun showAlertPaymentError(error: String){
        android.app.AlertDialog.Builder(activity!!)
            .setTitle("Payment Failed!")
            .setMessage("Error: $error")
            .setPositiveButton("Close") { dialog, _ ->
               dialog.dismiss()
            }
            .show()
    }

    private fun showAlertPaymentSuccess(){
        android.app.AlertDialog.Builder(activity!!)
            .setTitle("Payment Success")
            .setMessage("Your payment  was successful\nTransaction Id: $transactionId")
            .setPositiveButton("Close") { _, _ ->
                activity!!.finish()
            }
            .show()
    }

    private fun paymentUpdateJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        var jsonArray = JSONArray()
        try {
            for (item in userSelectionList){
                var jObj = JSONObject()
                jObj.put("booktimemasterid", item.itemId)
                jObj.put("pay_type", paymentMode)
                jObj.put("transid", transactionId)
                if (!item.itemTotal.isNullOrEmpty() && item.itemTotal.toInt() > 500){
                    jObj.put("redeem_status", "1")
                }else{
                    jObj.put("redeem_status", "0")
                }
                jsonArray.put(jObj)
            }
            json = JSONObject()
            json.put("paymentcustomer", jsonArray)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    fun updatePaymentStatus(transId: String){
        transactionId = transId
        showPaymentProgress(true)
        lifecycleScope.launch {
            val  response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                insertPaymentToServer(paymentUpdateJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message!! == "Success"){
                AppLogger.info("update to server", "success")
            }
            showAlertPaymentSuccess()
            showPaymentProgress(false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pending_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setListeners()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getPaymentList()
        }elseDo {
            showEmptyCaution(true)
            activity!!.toast {
                message = "No Internet Connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
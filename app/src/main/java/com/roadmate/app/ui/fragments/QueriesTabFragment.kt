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
import com.roadmate.app.adapter.FaqAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.FaqMaster
import com.roadmate.app.api.response.FaqTrans
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.ui.activities.AddCustomerVehicleActivity
import com.roadmate.app.ui.activities.AnswerQuestionActivity
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_querirs_tab.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class QueriesTabFragment: Fragment() {

    var customerQuestion = ""

    private fun customerPostAQuestion(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                askAQuestion(postQuestionJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message!! == "Success"){
                editText.setText("")
                getAllFaqQuestions()
            }else{
                activity!!.toast {
                    message = "OOPS!! Something went wrong!"
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(false)
        }
    }

    private fun getAllFaqQuestions(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<FaqMaster>> {
                getAllFaqs()
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateFAQs(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun populateFAQs(list: ArrayList<FaqTrans>){
        list.reverse()
        queries_recycler.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter = FaqAdapter(activity!!, list){obj ->
            val intent = Intent(activity, AnswerQuestionActivity::class.java)
            intent.putExtra("question", obj!!.question)
            intent.putExtra("qauthor", obj!!.quname)
            intent.putExtra("questionid", obj!!.quid)
            startActivity(intent)
        }
        queries_recycler.adapter = adapter
    }

    private fun postQuestionJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("question", editText.text.toString())
            json.put("quserid", UserDetails().userId)
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
        return inflater.inflate(R.layout.fragment_querirs_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        post_query.setOnClickListener {
            customerQuestion = editText.text.toString().trim()
            if (customerQuestion.isNotEmpty()){
                NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
                    customerPostAQuestion()
                }elseDo {
                    activity!!.toast {
                        message = "No internet connectivity!"
                        duration = Toast.LENGTH_SHORT
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getAllFaqQuestions()
        }elseDo {
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }
    }
}
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
import com.roadmate.app.adapter.FaqAdapter
import com.roadmate.app.adapter.FaqAnswerAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.*
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.extensions.toast
import com.roadmate.app.preference.UserDetails
import com.roadmate.app.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_answer_question.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class AnswerQuestionFragment: Fragment() {

    private fun getAllFaqAnswers(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<FaqAnswersMaster>> {
                getAllFaqAns(faqListJsonRequest())
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateFAQAnswers(response.body()?.data!!)
            }
            showProgress(false)
        }
    }

    private fun postAnswer(){
        showProgress(true)
        lifecycleScope.launch {
            val response = APIManager.call<ApiServices, Response<RoadmateApiResponse>> {
                answerAQuestion(postAnswerJsonRequest())
            }
            if (response.isSuccessful && response.body()?.message == "Success"){
                commentEditText.setText("")
                activity!!.toast {
                    message = "Your answer is posted."
                    duration = Toast.LENGTH_SHORT
                }
                activity!!.finish()
            }else{
                activity!!.toast {
                    message = "OOPS! Something went wrong."
                    duration = Toast.LENGTH_SHORT
                }
            }
            showProgress(true)
        }
    }

    private fun faqListJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("questionid", arguments!!["questionid"])
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun postAnswerJsonRequest() : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("questionid", arguments!!["questionid"])
            json.put("anusid", UserDetails().userId)
            json.put("answer", commentEditText.text.toString())
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populateFAQAnswers(list: ArrayList<FaqAnswersTrans>){
        list.reverse()
        faq_answers.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter = FaqAnswerAdapter(activity!!, list){ obj ->

        }
        faq_answers.adapter = adapter
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
        return inflater.inflate(R.layout.fragment_answer_question, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        NetworkUtils.isNetworkConnected(activity!!).doIfTrue {
            getAllFaqAnswers()
        }elseDo {
            activity!!.toast {
                message = "No internet connectivity!"
                duration = Toast.LENGTH_SHORT
            }
        }

        txtQuestion.text = "Q: " + arguments!! ["question"].toString()
        txtAuthor.text = arguments!! ["qauthor"].toString()

        sendReply.setOnClickListener {
            if (commentEditText.text.toString().trim().isNotEmpty()){
                postAnswer()
            }
        }
    }
}
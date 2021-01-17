package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.FaqAnswersTrans
import com.roadmate.app.api.response.FaqTrans

class FaqAdapter  internal constructor(private val context: Context, private val mData: ArrayList<FaqTrans>, val clickHandler: (obj: FaqTrans?) -> Unit) : RecyclerView.Adapter<FaqAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var txtQuestion: TextView = itemView.findViewById(R.id.txtQuestion)
        internal var txtAuthor: TextView = itemView.findViewById(R.id.txtAuthor)
        internal var commentEditText: TextView = itemView.findViewById(R.id.commentEditText)
        internal var sendReply: ImageView = itemView.findViewById(R.id.sendReply)
        internal var layoutAnwer1: LinearLayout = itemView.findViewById(R.id.layoutAnwer1)
        internal var txtAnswer1: TextView = itemView.findViewById(R.id.txtAnswer1)
        internal var txtAnsAuthor1: TextView = itemView.findViewById(R.id.txtAnsAuthor1)
        internal var view_all_answer: TextView = itemView.findViewById(R.id.view_all_answer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_faq, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.txtQuestion.text = "Q: " + data.question
        holder.txtAuthor.text = data.quname

        if (data.answer !=null && data.answer.isNotEmpty()){
            holder.view_all_answer.visibility = View.VISIBLE
            holder.layoutAnwer1.visibility = View.VISIBLE
            holder.txtAnswer1.text = "A: " + data.answer
            holder.txtAnsAuthor1.text = data.anname
        }else{
            holder.view_all_answer.visibility = View.GONE
            holder.layoutAnwer1.visibility = View.GONE
        }

        holder.commentEditText.setOnClickListener {
            clickHandler(data)
        }

        holder.layoutAnwer1.setOnClickListener {
            clickHandler(data)
        }
        holder.sendReply.setOnClickListener {
            clickHandler(data)
        }
    }
}
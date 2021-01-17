package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.FaqAnswersTrans
import com.roadmate.app.api.response.FaqTrans

class FaqAnswerAdapter  internal constructor(private val context: Context, private val mData: ArrayList<FaqAnswersTrans>, val clickHandler: (obj: FaqAnswersTrans?) -> Unit) : RecyclerView.Adapter<FaqAnswerAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var txtAnswer: TextView = itemView.findViewById(R.id.txtAnswer)
        internal var txtAuthor: TextView = itemView.findViewById(R.id.txtAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_faq_answer_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.txtAnswer.text = "A: " + data.answer
        holder.txtAuthor.text = data.auserid


    }
}
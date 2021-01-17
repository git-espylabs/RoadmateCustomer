package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.TransactionHistoryTrans

class TransactionHistoryAdapter  internal constructor(private val context: Context, private var mData: ArrayList<TransactionHistoryTrans>, val clickHandler: (obj: TransactionHistoryTrans?) -> Unit) : RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var price: TextView = itemView.findViewById(R.id.price)
        internal var dates: TextView = itemView.findViewById(R.id.dates)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_transaction_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.price.text = data.name
        holder.dates.text = data.mobile
    }
}
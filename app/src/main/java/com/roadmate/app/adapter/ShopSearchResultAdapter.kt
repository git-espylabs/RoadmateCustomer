package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.SearchTrans
import com.roadmate.app.api.response.ShopBookingListTrans

class ShopSearchResultAdapter  internal constructor(private val context: Context, private val mData: ArrayList<SearchTrans>, val clickHandler: (obj: SearchTrans?) -> Unit) : RecyclerView.Adapter<ShopSearchResultAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shName: TextView = itemView.findViewById(R.id.shName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.shName.text = data.shopname
    }
}
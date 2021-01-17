package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.BookedHistoryTrans

class BookedHistoryListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<BookedHistoryTrans>, val clickHandler: (obj: BookedHistoryTrans?) -> Unit) : RecyclerView.Adapter<BookedHistoryListAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shname: TextView = itemView.findViewById(R.id.shname)
        internal var shaddress: TextView = itemView.findViewById(R.id.shaddress)
        internal var bookedDate: TextView = itemView.findViewById(R.id.bookedDate)
        internal var bookedTime: TextView = itemView.findViewById(R.id.bookedTime)
        internal var vtnViewShop: Button = itemView.findViewById(R.id.vtnViewShop)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_booked_history_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.shname.text = data.shopname
        holder.shaddress.text = data.address
        holder.bookedDate.text = data.adate
        holder.bookedTime.text = data.timeslots

        holder.vtnViewShop.setOnClickListener {
            clickHandler(data)
        }
    }
}
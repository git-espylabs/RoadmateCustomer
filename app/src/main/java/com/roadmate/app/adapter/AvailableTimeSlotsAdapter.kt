package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.AvailableTimeSlotsTrans
import com.roadmate.app.api.response.CustomerOfferTrans
import com.roadmate.app.utils.CommonUtils
import org.jetbrains.anko.backgroundDrawable

class AvailableTimeSlotsAdapter  internal constructor(private val context: Context, private val mData: ArrayList<AvailableTimeSlotsTrans>, val clickHandler: (obj: AvailableTimeSlotsTrans?, isSelected: Boolean) -> Unit) : RecyclerView.Adapter<AvailableTimeSlotsAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shtime: TextView = itemView.findViewById(R.id.shtime)
        internal var lay1: LinearLayout = itemView.findViewById(R.id.lay1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_shop_timings, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.shtime.text = CommonUtils.formatTime_Hmmss(data.name)

        if (data.isAvailable == "0" && data.isSelected){
            holder.lay1.setBackgroundResource(R.drawable.capsule_button_green);
            holder.shtime.setTextColor(ContextCompat.getColor(context, R.color.white))
        }else if (data.isAvailable == "0" && !data.isSelected){
            holder.lay1.setBackgroundResource(R.drawable.capsule_button_plain);
            holder.shtime.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
        else{
            holder.lay1.setBackgroundResource(R.drawable.capsule_button_two);
            holder.shtime.setTextColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.shtime.setOnClickListener {
            if (data.isAvailable == "0"){
                if (data.isSelected){
                    data.isSelected = false
                    clickHandler(data, false)
                }else{
                    data.isSelected = true
                    clickHandler(data, true)
                }
            }
        }
    }
}
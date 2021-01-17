package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.PackageShopsTrans

class ShopsInPackageAdapter  internal constructor(private val context: Context, private val mData: ArrayList<PackageShopsTrans>, val clickHandler: (shopId: String?) -> Unit) : RecyclerView.Adapter<ShopsInPackageAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mechanic_name: TextView = itemView.findViewById(R.id.mechanic_name)
        internal var mechanic_distance: TextView = itemView.findViewById(R.id.mechanic_distance)
        internal var mechanic_location: TextView = itemView.findViewById(R.id.mechanic_location)
        internal var mechanic_time: TextView = itemView.findViewById(R.id.mechanic_time)
        internal var booknow: Button = itemView.findViewById(R.id.booknow)
        internal var booked: Button = itemView.findViewById(R.id.booked)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.shop_from_package_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.mechanic_name.text = data.shopname
        holder.mechanic_time.text = data.open_time + " - " + data.close_time
        holder.mechanic_distance.text = data.distance
        holder.mechanic_location.text = data.shopAddress
        /*if (data.bookingStatus == 0) {
            holder.booked.visibility = View.GONE
            holder.booknow.visibility = View.VISIBLE
        } else {
            holder.booked.visibility = View.VISIBLE
            holder.booknow.visibility = View.GONE
        }*/
        holder.booknow.visibility = View.VISIBLE

        holder.booknow.setOnClickListener {
            data.bookingStatus = 1
            clickHandler(data.shopId)
        }

    }
}
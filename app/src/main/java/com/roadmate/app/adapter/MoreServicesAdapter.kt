package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.response.MoreServicesTrans
import com.roadmate.app.api.response.RatedShopsTrans
import com.squareup.picasso.Picasso

class MoreServicesAdapter  internal constructor(private val context: Context, private val mData: ArrayList<MoreServicesTrans>, private val viewCount: Int, val clickHandler: (catId: String?, catName: String?) -> Unit) : RecyclerView.Adapter<MoreServicesAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var moreimage: ImageView = itemView.findViewById(R.id.moreimage)
        internal var moreservice: TextView = itemView.findViewById(R.id.moreservice)
        internal var moreLayout: LinearLayout = itemView.findViewById(R.id.moreLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.single_moreservice_recycler_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return viewCount
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val service: MoreServicesTrans = mData[position]

        holder.moreservice.text = service.serviceName

        Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + service.serviceImage)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.moreimage)

        holder.moreLayout.setOnClickListener {
            clickHandler(service.serviceId, service.serviceName)
        }
    }
}
package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.response.MoreServicesTrans
import com.roadmate.app.api.response.NearShopsTrans
import com.squareup.picasso.Picasso

class AllShopsAdapter  internal constructor(private val context: Context, private val mData: ArrayList<NearShopsTrans>, val clickHandler: (shopId: String?) -> Unit) : RecyclerView.Adapter<AllShopsAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mechanic_image_view: ImageView = itemView.findViewById(R.id.mechanic_image_view)
        internal var mechanic_name: TextView = itemView.findViewById(R.id.mechanic_name)
        internal var mechanic_distance: TextView = itemView.findViewById(R.id.mechanic_distance)
        internal var mechanic_location: TextView = itemView.findViewById(R.id.mechanic_location)
        internal var mechanic_time: TextView = itemView.findViewById(R.id.mechanic_time)
        internal var road_assistance: TextView = itemView.findViewById(R.id.road_assistance)
        internal var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        internal var layout: LinearLayout = itemView.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.all_mechanicshop_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val shopData = mData[position]

        holder.mechanic_name.text = shopData.shopName
        holder.mechanic_distance.text = shopData.shopDistance + "Km"
        holder.mechanic_location.text = shopData.shopAddress
        holder.mechanic_time.text = shopData.open_time + " - " + shopData.close_time

        Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + shopData.shopImage)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.mechanic_image_view)

        if (shopData.isRoadAssistanceAvailable == "1"){
            holder.road_assistance.visibility =View.VISIBLE
        }else{
            holder.road_assistance.visibility =View.GONE
        }

        try {
            val ratingValue: Float = if (!shopData.shopRating.isNullOrEmpty()) {
                shopData.shopRating.toFloat()
            } else {
                0F
            }
            /*if (ratingValue.toDouble() == 0.0) {
                holder.ratingBar.visibility = View.GONE
            } else {
                holder.ratingBar.rating = ratingValue
            }*/
            holder.ratingBar.rating = ratingValue
        } catch (e: Exception) {
            holder.ratingBar.visibility = View.GONE
        }

        holder.layout.setOnClickListener {
            clickHandler(shopData.shopId)
        }


    }
}
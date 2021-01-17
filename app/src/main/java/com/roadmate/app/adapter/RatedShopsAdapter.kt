package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.RatedShopsTrans
import com.squareup.picasso.Picasso

class RatedShopsAdapter  internal constructor(private val context: Context, private val mData: ArrayList<RatedShopsTrans>, val clickHandler: (shopId: String?) -> Unit) : RecyclerView.Adapter<RatedShopsAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var mechanic_image_view: ImageView = itemView.findViewById(R.id.mechanic_image_view)
        internal var mechanic_name: TextView = itemView.findViewById(R.id.mechanic_name)
        internal var mechanic_distance: TextView = itemView.findViewById(R.id.mechanic_distance)
        internal var mechanic_location: TextView = itemView.findViewById(R.id.mechanic_location)
        internal var mechanic_time: TextView = itemView.findViewById(R.id.mechanic_time)
        internal var ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        internal var road_assistance: FrameLayout = itemView.findViewById(R.id.road_assistance)
        internal var layout: LinearLayout = itemView.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.rated_shops_recyclerview_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var shopData = mData[0]

        holder.mechanic_name.text = shopData.shopName
        holder.mechanic_distance.text = shopData.shopDiastance
        holder.mechanic_location.text = shopData.shopLocation
        holder.mechanic_time.text = shopData.shopTime

        Picasso.with(context).load(shopData.shopImage).into(holder.mechanic_image_view)

        if (shopData.isRoadAssistanceAvailable == "1"){
            holder.road_assistance.visibility =View.VISIBLE
        }else{
            holder.road_assistance.visibility =View.GONE
        }

        val ratingValue: Float = shopData.shopRating.toFloat()
        if (ratingValue.toDouble() == 0.0) {
            holder.ratingBar.visibility = View.GONE
        } else {
            holder.ratingBar.rating = ratingValue
        }

        holder.layout.setOnClickListener {
            clickHandler(shopData.shopId)
        }
    }
}
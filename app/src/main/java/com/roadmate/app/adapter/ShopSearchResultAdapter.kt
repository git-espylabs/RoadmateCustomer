package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.response.SearchTrans
import com.roadmate.app.api.response.ShopBookingListTrans

class ShopSearchResultAdapter  internal constructor(private val context: Context, private val mData: ArrayList<SearchTrans>, val clickHandler: (obj: SearchTrans?) -> Unit) : RecyclerView.Adapter<ShopSearchResultAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var shName: TextView = itemView.findViewById(R.id.shName)
        internal var mainlay: CardView = itemView.findViewById(R.id.mainlay)
        internal var shopimg: ImageView = itemView.findViewById(R.id.shopimg)
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

        Glide.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + data.image)
            .error(R.drawable.road_mate_plain).error(R.drawable.road_mate_plain)
            .into(holder.shopimg)

        holder.mainlay.setOnClickListener {
            clickHandler(data)
        }
    }
}
package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.response.CustomerOfferTrans
import com.roadmate.app.api.response.PackageTrans
import com.squareup.picasso.Picasso

class OfferListAdapter  internal constructor(private val context: Context, private val mData: ArrayList<CustomerOfferTrans>, val clickHandler: (obj: CustomerOfferTrans?) -> Unit) : RecyclerView.Adapter<OfferListAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var offer_name: TextView = itemView.findViewById(R.id.offer_name)
        internal var package_amount: TextView = itemView.findViewById(R.id.package_amount)
        internal var package_offeramount: TextView = itemView.findViewById(R.id.package_offeramount)
        internal var package_percentage: TextView = itemView.findViewById(R.id.package_percentage)
        internal var package_image: ImageView = itemView.findViewById(R.id.package_image)
        internal var btnBookNow: TextView = itemView.findViewById(R.id.btnBookNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_offer_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var packageData = mData[position]

        holder.offer_name.text = packageData.title

        if (packageData.offer_discount_type == "1") {
            holder.package_amount.visibility = View.VISIBLE
            holder.package_percentage.visibility = View.GONE
            if (packageData.normalAMount != packageData.offerAmount) {
                holder.package_amount.text = context.getString(R.string.Rs) + packageData.normalAMount
                holder.package_offeramount.text = context.getString(R.string.Rs) + packageData.offerAmount
                holder.package_offeramount.visibility = View.VISIBLE
            } else {
                holder.package_amount.text = packageData.normalAMount
                holder.package_offeramount.visibility = View.GONE
            }
        }else{
            holder.package_amount.visibility = View.GONE
            holder.package_offeramount.visibility = View.GONE
            holder.package_percentage.visibility = View.VISIBLE
            holder.package_percentage.text = packageData.percentage + "% " + "Discount"
        }

        Picasso.with(context).load(BuildConfig.OFFER_URL_ENDPOINT + packageData.image).into(holder.package_image)

        holder.btnBookNow.setOnClickListener {
            clickHandler(packageData)
        }
    }
}
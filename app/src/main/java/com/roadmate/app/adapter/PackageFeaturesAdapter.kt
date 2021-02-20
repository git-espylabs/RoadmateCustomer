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
import com.roadmate.app.R
import com.roadmate.app.api.response.NearShopsTrans
import com.roadmate.app.api.response.PackageFeaturesTrans

class PackageFeaturesAdapter  internal constructor(private val context: Context, private val mData: ArrayList<PackageFeaturesTrans>) : RecyclerView.Adapter<PackageFeaturesAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var package_feature: TextView = itemView.findViewById(R.id.package_feature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.package_feature_recycler_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        if (data.feature != null && data.feature.isNotEmpty()) {
            holder.package_feature.text = data.feature
        }
    }
}
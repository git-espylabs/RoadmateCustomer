package com.roadmate.app.adapter

import android.R.attr.*
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.BuildConfig
import com.roadmate.app.R
import com.roadmate.app.api.response.PackageTrans
import com.squareup.picasso.Picasso


class PackageAdapter  internal constructor(
    private val context: Context,
    private val mData: ArrayList<PackageTrans>,
    val setOffset: Boolean,
    val clickHandler: (packageId: String?) -> Unit
) : RecyclerView.Adapter<PackageAdapter.ViewHolder>()  {


    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var package_name: TextView = itemView.findViewById(R.id.package_name)
        internal var package_amount: TextView = itemView.findViewById(R.id.package_amount)
        internal var package_offeramount: TextView = itemView.findViewById(R.id.package_offeramount)
        internal var package_image: ImageView = itemView.findViewById(R.id.package_image)
        internal var framelayout: FrameLayout = itemView.findViewById(R.id.framelayout)
        internal var main: CardView = itemView.findViewById(R.id.main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.packages_recyclerview_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getScreenWidth(): Int{
        val displayMetrics = context.resources.displayMetrics
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
//        return (width / displayMetrics.density).toInt()
        return width
    }

    fun Context.dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    fun Context.pxToDp(px: Int): Int {
        return (px / resources.displayMetrics.density).toInt()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var packageData = mData[position]

        if (setOffset) {
            val widthOffset = context.dpToPx(100)
            val params = LinearLayout.LayoutParams(
                (getScreenWidth() - widthOffset), LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val rMrgn = context.dpToPx(20)
            params.setMargins(0, 0, rMrgn, 0)

            holder.main.layoutParams = params
        }

        holder.package_name.text = packageData.packageName

        if (packageData.packagePrice != packageData.packageOfferPrice) {
            holder.package_amount.text = context.getString(R.string.Rs) + packageData.packagePrice
            holder.package_offeramount.text = context.getString(R.string.Rs) + packageData.packageOfferPrice
            holder.package_offeramount.visibility = View.VISIBLE
        } else {
            holder.package_amount.text = packageData.packagePrice
            holder.package_offeramount.visibility = View.GONE
        }

        Picasso.with(context).load(BuildConfig.BANNER_URL_ENDPOINT + packageData.packageImage).into(
            holder.package_image
        )

        holder.package_image.setOnClickListener {
            clickHandler(packageData.packageId)
        }

        holder.package_name.text = packageData.packageName
    }
}
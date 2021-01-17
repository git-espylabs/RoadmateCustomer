package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.CustomerVehicleTrans
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_FOUR_WHEELER
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_THREE_WHEELER
import com.roadmate.app.constants.AppConstants.Companion.VEHICLE_TYPE_TWO_WHEELER
import com.squareup.picasso.Picasso

class DefaultVehicleAdapter
internal constructor(
    private val context: Context,
    private val mData: ArrayList<CustomerVehicleTrans>,
    private val isEditable: Boolean,
    val clickHandler: (vehicleData: CustomerVehicleTrans?, position: Int, isDelete: Boolean, isEdit: Boolean) -> Unit) : RecyclerView.Adapter<DefaultVehicleAdapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var v_number: TextView = itemView.findViewById(R.id.v_number)
        internal var v_model: TextView = itemView.findViewById(R.id.v_model)
        internal var v_type: ImageView = itemView.findViewById(R.id.v_type)
        internal var v_options: ImageView = itemView.findViewById(R.id.v_options)
        internal var framelayout: FrameLayout = itemView.findViewById(R.id.framelayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.my_vehicle_bottomsheet_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var vehicle = mData[position]

        holder.v_number.text = vehicle.vehicleNumber
        holder.v_model.text = vehicle.vehicleBrandName + " " + vehicle.vehicleModelName

        if (isEditable){
            holder.v_options.visibility = View.VISIBLE
        }else{
            holder.v_options.visibility = View.GONE
        }

        when{
            vehicle.vehicleTypeName.equals(VEHICLE_TYPE_TWO_WHEELER, true) -> Picasso.with(context).load(R.drawable.two_wheeler).into(holder.v_type)

            vehicle.vehicleTypeName.equals(VEHICLE_TYPE_FOUR_WHEELER, true) -> Picasso.with(context).load(R.drawable.four_wheeler).into(holder.v_type)

            vehicle.vehicleTypeName.equals(VEHICLE_TYPE_THREE_WHEELER, true) -> Picasso.with(context).load(R.drawable.three_wheeler).into(holder.v_type)

            else -> Picasso.with(context).load(R.drawable.heavy_vehicle).into(holder.v_type)
        }

        holder.framelayout.setOnClickListener {
            clickHandler(vehicle, position, false, false)
        }

        holder.v_options.setOnClickListener {
            val popup = PopupMenu(context, holder.v_options)
            popup.menuInflater.inflate(R.menu.vehicle_options_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                val menu = item.title.toString()
                when {
                    menu.equals("Edit", ignoreCase = true) -> {
                        clickHandler(vehicle, position, false, true)
                    }
                    menu.equals("Delete", ignoreCase = true) -> {
                        clickHandler(vehicle, position, true, false)
                    }
                }
                true
            }
            popup.show()
        }
    }
}
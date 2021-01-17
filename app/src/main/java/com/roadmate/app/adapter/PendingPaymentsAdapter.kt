package com.roadmate.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.api.response.PaymentItemTrans
import com.roadmate.app.extensions.doIfFalse
import com.roadmate.app.extensions.doIfTrue
import com.roadmate.app.extensions.elseDo
import com.roadmate.app.rmapp.AppSession

class PendingPaymentsAdapter  internal constructor(private val context: Context, private val mData: ArrayList<PaymentItemTrans>, val clickHandler: (obj: PaymentItemTrans) -> Unit) : RecyclerView.Adapter<PendingPaymentsAdapter.ViewHolder>()  {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var accountBalance = 0;
    private  var totalRedeem = 0;

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cbShName: CheckBox = itemView.findViewById(R.id.cbShName)
        internal var billDate: TextView = itemView.findViewById(R.id.billDate)
        internal var amount: TextView = itemView.findViewById(R.id.amount)
        internal var radeemPayLay: RelativeLayout = itemView.findViewById(R.id.radeemPayLay)
        internal var radeemAmount: TextView = itemView.findViewById(R.id.radeemAmount)
        internal var sumAmount: TextView = itemView.findViewById(R.id.sumAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.row_item_pending_payments, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var data = mData[position]

        holder.cbShName.text = data.itemShopName
        holder.billDate.text = "Billing Date: " + data.itemShopServiceDate

        var billAmnt = 0.00
        var sum = 0.00

        if (!data.itemTotal.isNullOrEmpty() && data.itemTotal != "0"){
            billAmnt = data.itemTotal.toDouble()
            sum = billAmnt;
            holder.amount.text = context.resources.getString(R.string.Rs) + String.format("%.2f",  billAmnt)
        }else{
            holder.amount.text = context.resources.getString(R.string.Rs) + "0.00"
        }

        if (data.isItemSelected) {
            if (billAmnt > 500 && data.isRedeemed){
                holder.radeemPayLay.visibility = View.VISIBLE
                sum = billAmnt - 50;
            }else if (billAmnt > 500 && !data.isRedeemed && accountBalance >= 50){
                holder.radeemPayLay.visibility = View.VISIBLE
                sum = billAmnt - 50;
                accountBalance -= 50
                data.isRedeemed = true
            }else{
                holder.radeemPayLay.visibility = View.GONE
            }
        }else{
            holder.radeemPayLay.visibility = View.GONE
        }

        holder.sumAmount.text = context.resources.getString(R.string.Rs) + String.format("%.2f",  sum)
        holder.radeemAmount.text = context.resources.getString(R.string.Rs) + String.format("%.2f",  50.0)

        holder.cbShName.isChecked = data.isItemSelected

        holder.cbShName.setOnClickListener {
            if (!holder.cbShName.isChecked){
                if (billAmnt > 500 && data.isRedeemed){
                    accountBalance += 50
                    data.isRedeemed = false
                }
            }else{
                if (billAmnt > 500 && !data.isRedeemed && accountBalance >= 50){
                    accountBalance -= 50
                    data.isRedeemed = true
                }
            }
            data.isItemSelected = holder.cbShName.isChecked
            notifyDataSetChanged()
            clickHandler(data)
        }
    }
}
package com.roadmate.app.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.razorpay.PaymentResultListener
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.log.AppLogger
import com.roadmate.app.ui.fragments.CustomerAccountFragment
import com.roadmate.app.ui.fragments.PendingPaymentsFragment

class PendingPaymentListActivity : BaseActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.title = "Pending Payment"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setFragment(PendingPaymentsFragment(), FragmentConstants.PAYMENT_PENDING_FRAGMENT, null, false)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        AppLogger.error("OnPaymentError", "Exception in onPaymentError - $p1")
        var fragment: PendingPaymentsFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.PAYMENT_PENDING_FRAGMENT) as PendingPaymentsFragment
        fragment?.showAlertPaymentError(p1!!)
    }

    override fun onPaymentSuccess(p0: String?) {
        AppLogger.info("onPaymentSuccess", "Message:- $p0")
        var fragment: PendingPaymentsFragment? = supportFragmentManager.findFragmentByTag(FragmentConstants.PAYMENT_PENDING_FRAGMENT) as PendingPaymentsFragment
        fragment?.updatePaymentStatus(p0!!)
    }
}
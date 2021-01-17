package com.roadmate.app.ui.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.CustomerTransactionsHistoryFragment

class CustomerTransactionHistoryActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Transaction History"

        setFragment(CustomerTransactionsHistoryFragment(), FragmentConstants.CUSTOMER_TRANSACTION_HISTORY_FRAGMENT, null, false)
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
}
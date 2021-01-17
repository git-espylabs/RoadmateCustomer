package com.roadmate.app.ui.activities

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.AddCustomerVehicleFragment
import com.roadmate.app.ui.fragments.CustomerAccountFragment

class CustomerAccountActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.title = "Account"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        setFragment(CustomerAccountFragment(), FragmentConstants.CUSTOMER_ACCOUNT_FRAGMENT, null, false)
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
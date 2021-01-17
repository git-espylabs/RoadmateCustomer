package com.roadmate.app.ui.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.CustomerViewAllShopsFragment

class CustomerViewAllShopsActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = intent.getStringExtra("shopname")

        var bundle = Bundle()
        bundle.putString("shoptype", intent.getStringExtra("shoptype"))
        setFragment(CustomerViewAllShopsFragment(), FragmentConstants.CUSTOMER_VIEW_ALL_SHOPS_FRAGMENT, bundle, false)
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
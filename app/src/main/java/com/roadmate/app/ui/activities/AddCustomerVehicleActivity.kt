package com.roadmate.app.ui.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.AddCustomerVehicleFragment

class AddCustomerVehicleActivity : BaseActivity() {


    var vehicleId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.title = "Add your vehicle"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        try {
            if (intent.extras!!.containsKey("vehicle_id")) {
                vehicleId = intent.getStringExtra("vehicle_id")
            }
        } catch (e: Exception) {
        }

        var bundle = Bundle()
        bundle.putString("vehicle_id", vehicleId)

        setFragment(AddCustomerVehicleFragment(), FragmentConstants.ADD_CUSTOMER_VEHICLE_FRAGMENT, bundle, false)
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
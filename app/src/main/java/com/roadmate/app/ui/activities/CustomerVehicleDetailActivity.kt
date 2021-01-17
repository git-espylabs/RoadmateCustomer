package com.roadmate.app.ui.activities

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.CustomerVehicleDetailsFragment

class CustomerVehicleDetailActivity : BaseActivity() {

    var vehicleId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Detail"

        vehicleId = intent.getStringExtra("vehicle_id")
        var bundle = Bundle()
        bundle.putString("vehicle_id", vehicleId)

        setFragment(CustomerVehicleDetailsFragment(), FragmentConstants.CUSTOMER_VEHICLE_DETAILS_FRAGMENT, bundle, false)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.roadmate.app.R.menu.vehicle_top_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
                return true
            }
            com.roadmate.app.R.id.menu_edit -> {
                val intent = Intent(applicationContext, AddCustomerVehicleActivity::class.java)
                intent.putExtra("vehicle_id", vehicleId)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.roadmate.app.ui.activities

import android.R
import android.os.Bundle
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.ui.fragments.CustomerTransactionsHistoryFragment
import com.roadmate.app.ui.fragments.PackageDetailsFragment

class PackageDetailsActivity : BaseActivity() {

    var packageId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = ""

        packageId = intent.getStringExtra("PackageId")
        var bundle = Bundle()
        bundle.putString("packageId",packageId)

        setFragment(PackageDetailsFragment(), FragmentConstants.PACKAGE_DETAILS_FRAGMENT, bundle, false)
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
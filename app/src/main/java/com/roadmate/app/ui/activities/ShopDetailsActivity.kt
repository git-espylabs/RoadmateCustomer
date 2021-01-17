package com.roadmate.app.ui.activities

import android.R
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.roadmate.app.constants.FragmentConstants
import com.roadmate.app.rmapp.AppSession
import com.roadmate.app.rmapp.AppSession.Companion.selectedShopLocation
import com.roadmate.app.ui.fragments.ShopDetailsFragment

class ShopDetailsActivity : BaseActivity() {

    var shopId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Detail"

        shopId = intent.getStringExtra("shopId")

        var bundle = Bundle()
        bundle.putString("shopId", intent.getStringExtra("shopId"))

        setFragment(ShopDetailsFragment(), FragmentConstants.SHOP_DETAILS_FRAGMENT, bundle, false)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(com.roadmate.app.R.menu.map_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.roadmate.app.R.id.menu_search -> {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("geo:0,0?q=$selectedShopLocation")
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            com.roadmate.app.R.id.menu_call -> {

                val uri = "tel:" + AppSession.selectedShopPhone
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(uri)
                startActivity(intent)
            }
            R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
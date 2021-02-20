package com.roadmate.app.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.roadmate.app.R
import com.roadmate.app.adapter.ShopSearchResultAdapter
import com.roadmate.app.adapter.ShopsServiceBookingListAdapter
import com.roadmate.app.api.manager.APIManager
import com.roadmate.app.api.response.RoadmateApiResponse
import com.roadmate.app.api.response.SearchMater
import com.roadmate.app.api.response.SearchTrans
import com.roadmate.app.api.response.ShopBookingListTrans
import com.roadmate.app.api.service.ApiServices
import com.roadmate.app.preference.UserDetails
import kotlinx.android.synthetic.main.activity_search_suggestion.*
import kotlinx.android.synthetic.main.fragment_home_tab.*
import kotlinx.android.synthetic.main.fragment_shop_booking_list.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class SearchSuggestionActivity : AppCompatActivity() {

    var searchText =""
    private lateinit var searchView: SearchView

    private fun search(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                /* Intent intent = new Intent(getApplicationContext(), SearchExpand.class);
                startActivity(intent);*/
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchText = newText
                if (newText.length >= 3){
                    searchView.isEnabled = false
                    searchShop(newText)
                }
                return false
            }
        })
    }

    private fun searchShop(searchText: String){
        wordlist.visibility = View.GONE
        progressV.visibility = View.VISIBLE
        lifecycleScope.launch {
            val  response = APIManager.call<ApiServices, Response<SearchMater>> {
                shopSearch(searchRequest(searchText))
            }
            if (response.isSuccessful && response.body()?.data!!.isNotEmpty()){
                populateList(response.body()?.data!!)
            }else{
                no_results.visibility = View.VISIBLE
                wordlist.visibility = View.GONE
            }
            searchView.isEnabled = true
            progressV.visibility = View.GONE
        }
    }

    private fun searchRequest(searchText: String) : RequestBody {
        var jsonData = ""
        var json: JSONObject? = null
        try {
            json = JSONObject()
            json.put("shopname", searchText)
            jsonData = json.toString()
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonData.toRequestBody()
    }

    private fun populateList(list: ArrayList<SearchTrans>){
        no_results.visibility = View.GONE
        wordlist.visibility = View.VISIBLE

        wordlist.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter = ShopSearchResultAdapter(this, list){ obj ->
            val intent = Intent(this, ShopDetailsActivity::class.java)
            intent.putExtra("shopId", obj!!.id)
            startActivity(intent)
        }
        wordlist.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_suggestion)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val search = menu!!.findItem(R.id.action_search)
        searchView = MenuItemCompat.getActionView(search) as SearchView
        searchView.isIconified = false
        search(searchView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        this.finish()
    }
}
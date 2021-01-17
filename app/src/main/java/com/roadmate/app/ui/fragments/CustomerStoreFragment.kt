package com.roadmate.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.roadmate.app.R
import com.roadmate.app.adapter.TabAdapter
import kotlinx.android.synthetic.main.fragment_customer_sotre.*
import kotlinx.android.synthetic.main.fragment_customer_sotre.tabs

class CustomerStoreFragment: BaseFragment() {

    lateinit var tabAdapter: TabAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_customer_sotre, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tabAdapter = TabAdapter(activity!!.supportFragmentManager)
        tabAdapter.addFragment(StoreTabFragment(), "STORE")
        tabAdapter.addFragment(SellHereTabFragment(), "SELL HERE")
        tabAdapter.addFragment(QueriesTabFragment(), "QUERIES")
        viewPagerStore.adapter = tabAdapter
        tabs.setupWithViewPager(viewPagerStore)
    }
}
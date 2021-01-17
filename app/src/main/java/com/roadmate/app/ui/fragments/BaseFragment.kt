package com.roadmate.app.ui.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment


open class BaseFragment : Fragment(){

    fun setFragment(targetFragment: Fragment, fragmentTag: String?, bundle: Bundle?, addToBackStack: Boolean, container: Int) {
        val transaction =
            fragmentManager!!.beginTransaction()
        if (bundle != null) {
            targetFragment.arguments = bundle
        }
        transaction.replace(container, targetFragment, fragmentTag)
        if (addToBackStack) {
            transaction.addToBackStack(fragmentTag)
        } else {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }
}
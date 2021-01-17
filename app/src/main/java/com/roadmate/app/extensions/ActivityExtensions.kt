package com.roadmate.app.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.roadmate.app.rmapp.App

/**
 * Activity extension methods
 */

/**
 * Extension method to start an activity
 */
inline fun <reified T: Activity> Context.startActivity(){
    val intent = Intent(this, T::class.java)
    startActivity(intent)
}

/**
 * Extension method to begin fragment transaction
 */
inline fun FragmentManager.beginTransaction(op: FragmentTransaction.() -> FragmentTransaction){
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.op()
    fragmentTransaction.commit()
}

/**
 * Extension method to add fragment to FragmentManager
 */
fun AppCompatActivity.addFragment(tag: String, fragment: Fragment, frameId: Int){
    supportFragmentManager.beginTransaction { add(frameId, fragment, tag) }
}

/**
 * Extension method to return App class
 */
val Activity.app: App
    get() = application as App

/**
 * Extension method to check whether permission dialog should be shown to the user
 */
fun Activity.isUserCheckNeverAskAgain(permission: String) =
    !ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        permission
    )
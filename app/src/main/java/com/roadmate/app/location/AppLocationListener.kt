package com.roadmate.app.location

import android.location.Location

interface AppLocationListener{

    fun onLocationReceived(location: Location)

    fun onLocationSettingsSatisfied()
}
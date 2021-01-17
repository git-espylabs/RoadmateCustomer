package com.roadmate.app.rmapp

import android.content.Context
import com.razorpay.Checkout
import com.roadmate.app.api.response.*
import com.roadmate.app.preference.Preference
import com.roadmate.app.preference.UserDetails

class AppSession {
    companion object{

        var otpTemp = ""

        var userMobile = ""
        var vehicleTypeList: ArrayList<VehicleTypeTrans> = arrayListOf()
        var vehicleBrandList: ArrayList<VehicleBrandTrans> = arrayListOf()
        var vehicleModelList: ArrayList<VehicleModelTrans> = arrayListOf()
        var vehicleFuelTypeList: ArrayList<VehicleFuelTypeTrans> = arrayListOf()
        var myVehicleList: ArrayList<CustomerVehicleTrans> = arrayListOf()
        var selectedShopLocation = ""
        var selectedShopPhone = ""
        var appLatitude = "0.0"
        var appLongitude = "0.0"
        var appBannerList: ArrayList<AppBannerTrans> = arrayListOf()
        var packageBannerList: ArrayList<AppBannerTrans> = arrayListOf()
        var storeBannerList: ArrayList<AppBannerTrans> = arrayListOf()
        var myWalletBalance = 0;

        fun clearUserSession(context: Context){
            Preference.cleanPreferences()
            Checkout.clearUserData(context)
            vehicleFuelTypeList.clear()
            vehicleBrandList.clear()
            vehicleModelList.clear()
            vehicleFuelTypeList.clear()
            myVehicleList.clear()
            selectedShopLocation = ""
            selectedShopPhone = ""
            appLatitude = ""
            appLongitude = ""
            appBannerList.clear()
            packageBannerList.clear()
            storeBannerList.clear()
        }
    }
}
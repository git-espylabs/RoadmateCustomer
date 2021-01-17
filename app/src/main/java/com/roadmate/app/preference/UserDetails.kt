package com.roadmate.app.preference

import com.roadmate.app.constants.SharedPreferenceConstants

class UserDetails {

    var userId by Preference(SharedPreferenceConstants.USER_ID, "")
    var userType by Preference(SharedPreferenceConstants.USER_TYPE, "")
    var userMobile by Preference(SharedPreferenceConstants.USER_MOB, "")
    var userName by Preference(SharedPreferenceConstants.USER_NAME, "")
    var isUserLoggedIn by Preference(SharedPreferenceConstants.USER_LOGGED_IN, false)
    var isUserRegistered by Preference(SharedPreferenceConstants.IS_USER_REGISTERED, false)
    var isOtpVerified by Preference(SharedPreferenceConstants.IS_OTP_VERIFIED, false)
}
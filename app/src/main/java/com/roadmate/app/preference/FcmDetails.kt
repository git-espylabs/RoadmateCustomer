package com.roadmate.app.preference

import com.roadmate.app.constants.SharedPreferenceConstants

class FcmDetails {
    var fcmToken by Preference(SharedPreferenceConstants.FCM_TOKEN, "")
    var isFcmRegistered by Preference(SharedPreferenceConstants.FCM_REGISTERED, false)
}
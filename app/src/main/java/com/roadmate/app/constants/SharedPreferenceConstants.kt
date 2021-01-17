package com.roadmate.app.constants

/**
 * SharedPreferenceConstants - Different shared preference constants
 */
class SharedPreferenceConstants {
    companion object{
        const val PREF_NAME = "rmate_pref"

        const val SHOW_CASE_PRESENTED = "show_case_presented"
        const val SHOW_REGISTRATION_BONUS = "show_registration_bonus"

        const val USER_ID = "user_id"
        const val USER_TYPE = "user_type"
        const val USER_MOB = "user_mob"
        const val USER_NAME = "user_name"
        const val USER_LOGGED_IN = "user_logged_in"
        const val IS_USER_REGISTERED = "is_user_registered"
        const val IS_OTP_VERIFIED = "is_otp_verified"

        const val FCM_TOKEN = "fcm_token"
        const val FCM_REGISTERED = "is_fcm_registered"

        const val DEF_VEH_ID = "def_veh_id"
        const val DEF_VEH_NO = "def_veh_no"
        const val DEF_VEH_MODEL = "def_veh_model"
        const val DEF_VEH_MODEL_ID = "def_veh_model_id"
        const val DEF_VEH_TYPE = "def_veh_type"
        const val DEF_VEH_FUEL_TYPE = "def_veh_fuel_type"
    }
}
package com.roadmate.app.preference

import com.roadmate.app.constants.SharedPreferenceConstants

class DefaultVehicleDetails {

    var vehicleId by Preference(SharedPreferenceConstants.DEF_VEH_ID, "")
    var vehicleNo by Preference(SharedPreferenceConstants.DEF_VEH_NO, "")
    var vehicleModel by Preference(SharedPreferenceConstants.DEF_VEH_MODEL, "")
    var vehicleModelId by Preference(SharedPreferenceConstants.DEF_VEH_MODEL_ID, "")
    var vehicleType by Preference(SharedPreferenceConstants.DEF_VEH_TYPE, "")
    var vehicleFuelType by Preference(SharedPreferenceConstants.DEF_VEH_FUEL_TYPE, "")
}
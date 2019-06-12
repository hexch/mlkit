package com.experienceconnect.qrscanner.data

import android.content.SharedPreferences
import com.experienceconnect.qrscanner.utils.SP_KEY_INTERVAL
import com.experienceconnect.qrscanner.utils.SP_KEY_SERVER

class SettingsRepo(private val sp : SharedPreferences){
    fun getInterval() =sp.getInt(SP_KEY_INTERVAL,1)
    fun getServer()=sp.getString(SP_KEY_SERVER,null)
 }
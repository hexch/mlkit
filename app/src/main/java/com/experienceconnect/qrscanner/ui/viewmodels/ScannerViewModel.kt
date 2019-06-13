package com.experienceconnect.qrscanner.ui.viewmodels

import androidx.lifecycle.ViewModel;
import com.experienceconnect.qrscanner.data.SettingsRepo
import java.util.concurrent.TimeUnit

class ScannerViewModel (private val repo: SettingsRepo): ViewModel() {
    var intervaltemp = repo.getInterval()
    var interval  = TimeUnit.SECONDS.toMillis(1)
    var lastTimeStamp = 0L
    var server = repo.getServer()
    var pause = false
}

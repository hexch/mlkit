package com.experienceconnect.qrscanner.ui.viewmodels

import androidx.lifecycle.ViewModel;
import com.experienceconnect.qrscanner.data.SettingsRepo

class ScannerViewModel (private val repo: SettingsRepo): ViewModel() {
    var interval = repo.getInterval()
    var server = repo.getServer()
}

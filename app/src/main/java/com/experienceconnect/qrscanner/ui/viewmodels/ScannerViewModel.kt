package com.experienceconnect.qrscanner.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.experienceconnect.qrscanner.data.repos.HistoryRepo
import com.experienceconnect.qrscanner.data.repos.SettingsRepo
import java.util.concurrent.TimeUnit

class ScannerViewModel(private val settingsRepo: SettingsRepo, private val historyRepo: HistoryRepo) : ViewModel() {
    var settings = settingsRepo.getSettings()
    var historyList = historyRepo.getAll()

    var pause = false
    var lastTimeStamp:Long = 0

    fun isTimeout() : Boolean {
        val interval:Long = settings.value?.interval?.toLong()?:1L
        val current = System.currentTimeMillis()
        val isTimeout = current - lastTimeStamp > TimeUnit.SECONDS.toMillis(interval)
        if (isTimeout) lastTimeStamp = current
        return isTimeout
    }

}
package com.experienceconnect.qrscanner.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.experienceconnect.qrscanner.data.entities.Settings
import com.experienceconnect.qrscanner.data.repos.HistoryRepo
import com.experienceconnect.qrscanner.data.repos.SettingsRepo

class MainViewModel(private val settingsRepo: SettingsRepo,private val historyRepo: HistoryRepo) : ViewModel() {
    var settings = settingsRepo.getSettings()
    var historyList = historyRepo.getAll()

    fun insert(settings:Settings) = settingsRepo.insert(settings)

    var interval :String =settings.value?.interval.toString()
        set(value) {
            val s = settings.value!!
            s.interval = value.toInt()
            settingsRepo.update(s)
            field = value
        }

    var server :String=""

}

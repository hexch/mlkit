package com.experienceconnect.qrscanner.data.repos

import com.experienceconnect.qrscanner.data.daos.SettingsDao
import com.experienceconnect.qrscanner.data.entities.Settings

class SettingsRepo(val dao:SettingsDao) {
    fun getSettings() = dao.getSettings()
    fun update(settings: Settings) = dao.update(settings)
    fun insert(settings:Settings) = dao.insert(settings)
}
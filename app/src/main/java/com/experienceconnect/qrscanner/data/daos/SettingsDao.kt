package com.experienceconnect.qrscanner.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.experienceconnect.qrscanner.data.entities.Settings

@Dao
interface SettingsDao {
    @Query("select * from Settings limit 1")
    fun getSettings(): LiveData<Settings>
    @Update
    fun update(settings: Settings)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings:Settings)
}
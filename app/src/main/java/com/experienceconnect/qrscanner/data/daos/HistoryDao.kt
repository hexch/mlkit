package com.experienceconnect.qrscanner.data.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.experienceconnect.qrscanner.data.entities.History

@Dao
interface HistoryDao {
    @Query("select * from History order by id desc")
    fun getAll(): LiveData<List<History>>

    @Query("select * from History order by id ASC limit :limit")
    fun getLimit(limit: Int): LiveData<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyList: List<History>)
}
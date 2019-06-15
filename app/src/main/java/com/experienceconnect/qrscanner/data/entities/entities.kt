package com.experienceconnect.qrscanner.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Settings")
data class Settings(

    @ColumnInfo(name = "interval") var interval: Int = 1,
    @ColumnInfo(name = "server") var server: String?
){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}


@Entity(tableName = "History")
data class History(
    @ColumnInfo(name = "timestamp") var timestamp: Long?,
    @ColumnInfo(name = "raw") var raw: String?,
    @ColumnInfo(name = "server") var server: String?,
    @ColumnInfo(name = "status") var status: Boolean?

){
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}

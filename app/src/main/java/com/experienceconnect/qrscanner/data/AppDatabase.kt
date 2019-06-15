package com.experienceconnect.qrscanner.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.experienceconnect.qrscanner.data.daos.HistoryDao
import com.experienceconnect.qrscanner.data.daos.SettingsDao
import com.experienceconnect.qrscanner.data.entities.History
import com.experienceconnect.qrscanner.data.entities.Settings


@Database(entities = [Settings::class, History::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) { create(context) }.also { instance = it }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java, "mldb"
            ).addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                }
            })
                .build()
        }
        fun doAfterCreate(context: Context) {
            Log.d("AppDatabase","init1")
            val settingsDao = AppDatabase.getInstance(context).settingsDao()
            if (settingsDao.getSettings().value == null){
                Log.d("AppDatabase","init")
                val settings = Settings(server = "")
                settingsDao.insert(settings)
            }
        }
    }
}
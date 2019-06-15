package com.experienceconnect.qrscanner

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.experienceconnect.qrscanner.data.AppDatabase
import com.experienceconnect.qrscanner.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp:Application(){
    override fun onCreate() {
        super.onCreate()
        Room.databaseBuilder(
            this,
            AppDatabase::class.java, "mldb"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }).build()
        startKoin{
            androidLogger()
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}
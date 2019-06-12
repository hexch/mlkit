package com.experienceconnect.qrscanner

import android.app.Application
import com.experienceconnect.qrscanner.koin.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp:Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@MyApp)
            modules(appModule)
        }
    }
}
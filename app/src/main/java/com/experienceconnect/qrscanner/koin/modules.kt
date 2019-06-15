package com.experienceconnect.qrscanner.koin

import android.content.Context
import android.content.SharedPreferences
import com.experienceconnect.qrscanner.data.AppDatabase
import com.experienceconnect.qrscanner.data.daos.HistoryDao
import com.experienceconnect.qrscanner.data.daos.SettingsDao
import com.experienceconnect.qrscanner.data.repos.HistoryRepo
import com.experienceconnect.qrscanner.data.repos.SettingsRepo
import com.experienceconnect.qrscanner.ui.viewmodels.MainViewModel
import com.experienceconnect.qrscanner.ui.viewmodels.ScannerViewModel
import com.experienceconnect.qrscanner.utils.SP_FILE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<SharedPreferences> { androidContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE) }
    single { AppDatabase.getInstance(androidContext())}
    single<HistoryDao> { get<AppDatabase>().historyDao() }
    single<SettingsDao> { get<AppDatabase>().settingsDao() }

    single { SettingsRepo(get()) }
    single { HistoryRepo(get()) }

    viewModel { MainViewModel(get(),get()) }
    viewModel { ScannerViewModel(get(),get()) }

}
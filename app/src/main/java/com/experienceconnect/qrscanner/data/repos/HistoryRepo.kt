package com.experienceconnect.qrscanner.data.repos

import com.experienceconnect.qrscanner.data.daos.HistoryDao
import com.experienceconnect.qrscanner.data.entities.History

class HistoryRepo(val dao: HistoryDao) {
    fun getAll() = dao.getAll()
    fun getLimit(limit: Int) = dao.getLimit(limit)
    fun insert(history: History) = dao.insert(history)
    fun insert(historyList: List<History>) = dao.insert(historyList)
}
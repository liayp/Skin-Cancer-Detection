package com.dicoding.asclepius.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.HistoryDatabase
import com.dicoding.asclepius.data.local.HistoryEntity

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val historyDao = HistoryDatabase.getInstance(application).historyDao()
    val historyList: LiveData<List<HistoryEntity>> = historyDao.getAllHistories()
}

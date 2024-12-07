package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface HistoryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: HistoryEntity)

    @Delete
    suspend fun delete(event: HistoryEntity)

    @Query("SELECT * FROM history_analyze")
    fun getAllHistories(): LiveData<List<HistoryEntity>>

    @Query("SELECT * FROM history_analyze WHERE id = :id")
    fun getHistoryById(id: Int): LiveData<HistoryEntity?>
}

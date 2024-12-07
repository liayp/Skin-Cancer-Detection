package com.dicoding.asclepius.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import kotlinx.parcelize.Parcelize

@Entity(tableName = "history_analyze")
@Parcelize
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "imageUri")
    var uri: String,

    @ColumnInfo(name = "label")
    var label: String? = null,

    @ColumnInfo(name = "confidence")
    var confidence: Float = 0.0F,

    @ColumnInfo(name = "dateGenerate")
    val dateGenerate: String? = null
) : Parcelable

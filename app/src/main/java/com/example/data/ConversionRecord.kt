package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_records")
data class ConversionRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val sourceFormat: String,
    val targetFormat: String,
    val fileSize: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String, // "Pending", "Converting", "Success", "Failed"
    val isFavorite: Boolean = false,
    val resultFilePath: String? = null
)

package com.example.data

import kotlinx.coroutines.flow.Flow

class ConversionRepository(private val conversionDao: ConversionDao) {
    val allRecords: Flow<List<ConversionRecord>> = conversionDao.getAllRecords()
    val favoriteRecords: Flow<List<ConversionRecord>> = conversionDao.getFavoriteRecords()

    suspend fun insert(record: ConversionRecord): Long {
        return conversionDao.insertRecord(record)
    }

    suspend fun update(record: ConversionRecord) {
        conversionDao.updateRecord(record)
    }

    suspend fun deleteById(id: Int) {
        conversionDao.deleteRecordById(id)
    }

    suspend fun clear() {
        conversionDao.clearAll()
    }
}

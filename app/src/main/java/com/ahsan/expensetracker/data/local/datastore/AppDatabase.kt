package com.ahsan.expensetracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ahsan.expensetracker.data.local.TransactionDao
import com.ahsan.expensetracker.models.Transaction

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getTransactionDao(): TransactionDao
}

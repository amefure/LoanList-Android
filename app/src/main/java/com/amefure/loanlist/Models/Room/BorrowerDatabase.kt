package com.amefure.loanlist.Models.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MoneyRecord::class, Borrower::class], version = 1, exportSchema = false)
abstract class BorrowerDatabase : RoomDatabase() {
    abstract fun dao(): BorrowerDao

    companion object {
        @Volatile
        private var INSTANCE: BorrowerDatabase? = null
        fun getDatabase(context: Context): BorrowerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BorrowerDatabase::class.java,
                    "borrower_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
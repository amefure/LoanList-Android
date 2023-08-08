package com.amefure.loanlist.Models.Room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [MoneyRecord::class, Borrower::class], version = 1, exportSchema = false)
abstract class MoneyRecordDatabase : RoomDatabase() {
    abstract fun dao(): MoneyRecordDao

    companion object {
        @Volatile
        private var INSTANCE: MoneyRecordDatabase? = null
        fun getDatabase(context: Context): MoneyRecordDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyRecordDatabase::class.java,
                    "record_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
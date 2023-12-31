package com.amefure.loanlist.Models.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable

@Dao
interface MoneyRecordDao {
    @Insert
    fun insertMoneyRecord(moneyRecord: MoneyRecord)

    @Update
    fun updateMoneyRecord(moneyRecord: MoneyRecord)

    @Delete
    fun deleteMoneyRecord(moneyRecord: MoneyRecord)

    @Query("DELETE FROM record_table WHERE borrower_id = :borrowerId")
    fun deleteAllMoneyRecord(borrowerId: Int)

    // 未使用
    // @Query("SELECT * FROM record_table WHERE borrower_id = :borrowerId")
    // fun getAllRecordsForBorrower(borrowerId: Int): Flowable<List<MoneyRecord>>
    @Query("SELECT * FROM record_table")
    fun getAllRecords(): Flowable<List<MoneyRecord>>

}
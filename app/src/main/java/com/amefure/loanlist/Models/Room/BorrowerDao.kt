package com.amefure.loanlist.Models.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable


@Dao
interface BorrowerDao {
    @Insert
    fun insertBorrower(borrower: Borrower)

    @Update
    fun updateBorrower(borrower: Borrower)

    @Delete
    fun deleteBorrower(borrower: Borrower)

    @Query("SELECT * FROM borrower_table")
    fun getAllBorrowers(): Flowable<List<Borrower>>

    // 未使用
    // @Query("SELECT * FROM borrower_table WHERE id = :id")
    // fun getBorrowerById(id: Int): Flowable<Borrower>
}
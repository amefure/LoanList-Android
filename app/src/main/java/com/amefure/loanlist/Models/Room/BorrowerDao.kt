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
    suspend fun insertBorrower(borrower: Borrower): Long

    @Update
    suspend fun updateBorrower(borrower: Borrower)

    @Delete
    suspend fun deleteBorrower(borrower: Borrower)

    @Query("SELECT * FROM borrower_table")
    fun getAllBorrowers(): Flowable<List<Borrower>>

    @Query("SELECT * FROM borrower_table WHERE id = :id")
    fun getBorrowerById(id: Int): Flowable<Borrower>

}
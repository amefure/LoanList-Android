package com.amefure.loanlist.Models.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "record_table",
//    foreignKeys = [ForeignKey(
//        entity = Borrower::class,
//        parentColumns = ["id"],
//        childColumns = ["borrower_id"],
//        onDelete = ForeignKey.CASCADE
//    )],
//    indices = [Index("borrower_id")]
)
data class MoneyRecord (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id") val id: Int,
    val amount: Int,
    val desc: String,
    val borrow: Boolean,
    val date: String,
    @ColumnInfo(name = "borrower_id") val borrowerId: Int
)

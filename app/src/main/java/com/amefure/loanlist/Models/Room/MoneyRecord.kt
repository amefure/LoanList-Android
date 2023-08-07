package com.amefure.loanlist.Models.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "record_table",
    foreignKeys = [ForeignKey(
        entity = Borrower::class,
        parentColumns = ["id"],
        childColumns = ["borrower_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class MoneyRecord (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val amount: Int,
    val desc: String,
    val borrow: Boolean,
    val date: Date,
    @ColumnInfo(name = "borrower_id") val borrowerId: Int // Borrowerの外部キー
)

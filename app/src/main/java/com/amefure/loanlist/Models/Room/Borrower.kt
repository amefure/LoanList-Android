package com.amefure.loanlist.Models.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "borrower_table")
data class Borrower (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    @ColumnInfo(name = "return_flag") val returnFlag: Boolean,
)



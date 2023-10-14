package com.amefure.loanlist.Models.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "record_table",
// なぜか関連付けできず
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
    val amount: Long,  // 金額
    val desc: String,  // 内容
    val borrow: Boolean, // 借貸フラグ 真:貸付(オレンジ) 偽：借入(青)
    val date: String,
    @ColumnInfo(name = "borrower_id") val borrowerId: Int
)

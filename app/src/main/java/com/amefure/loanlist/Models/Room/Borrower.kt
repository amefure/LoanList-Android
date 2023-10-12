package com.amefure.loanlist.Models.Room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.temporal.TemporalAmount

@Entity(tableName = "borrower_table")
data class Borrower (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    // varで定義 リサイクルビューで変更するため
    @ColumnInfo(name = "return_flag") var returnFlag: Boolean,
    // アプリ内でアクティブになる借主
    val current: Boolean,
    // レコードの合計金額
    val amountSum: Long
){
    companion object {

        fun createFakes(): List<Borrower> {
            val now = System.currentTimeMillis()
            return List(10) {
                Borrower(
                    id = it,
                    name = "borrower $it",
                    returnFlag = false,
                    current = false,
                    amountSum = 0
                )
            }
        }
    }
}
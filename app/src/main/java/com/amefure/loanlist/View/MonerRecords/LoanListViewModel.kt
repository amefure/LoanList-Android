package com.amefure.loanlist.View.MonerRecords

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.loanlist.Models.DataStore.SortItem
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoanListViewModel(app:Application):RootViewModel(app) {
    private val _recordList = MutableLiveData<List<MoneyRecord>>()
    val recordList: LiveData<List<MoneyRecord>> = _recordList

    // 並び替え用にViewModel内で最新のものを保持しておく
    private var currentList: List<MoneyRecord> = listOf()

    private val _borrowerList = MutableLiveData<List<Borrower>>()
    public val borrowerList: LiveData<List<Borrower>> = _borrowerList

    // アクティブになっている借主の合計金額を取得するため
    fun loadBorrowerItems() {
        // データの取得は非同期で
        viewModelScope.launch (Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.loadBorrowerItems {
                _borrowerList.postValue(it)  // 本来はDBやCacheから取得
            }
        }
    }

    // レコード読み込み処理
    public fun loadRecordItems() {
        viewModelScope.launch (Dispatchers.IO) {
            rootRepository.loadMoneyRecords() {
                // 並び替え用保持プロパティにも保持
                currentList = it
                _recordList.postValue(it.sortedBy { it.date }.reversed())
            }
        }
    }


    // 読み込み済みのレコードを並び替える
    public fun recordItemsSorted(sortItem: SortItem) {
        when (sortItem) {
            SortItem.AMOUNT_ASCE -> {
                // 金額昇順
                _recordList.postValue(currentList.sortedBy { it.amount })
            }
            SortItem.AMOUNT_DESC  -> {
                // 金額降順
                _recordList.postValue(currentList.sortedBy { it.amount }.reversed())
            }
            SortItem.DATE_ASCE  -> {
                // 日付昇順
                _recordList.postValue(currentList.sortedBy { it.date })
            }
            SortItem.DATE_DESC  -> {
                // 日付降順
                _recordList.postValue(currentList.sortedBy { it.date }.reversed())
            }
            SortItem.BORROW_ONLY  -> {
                // 借のみかつ日付降順
                _recordList.postValue(currentList.filter { it.borrow == true }.sortedBy { it.date }.reversed())
            }
            SortItem.LOAN_ONLY  -> {
                // 貸のみかつ日付降順
                _recordList.postValue(currentList.filter { it.borrow == false }.sortedBy { it.date }.reversed())
            }
            else -> {
                // NONEは日付の降順にしておく
                _recordList.postValue(currentList.sortedBy { it.date }.reversed())
            }
        }
    }
    public fun deleteMoneyRecord(record: MoneyRecord) {
        // 削除された際に借主のsumプロパティも操作する
        subAmount(record.borrowerId, record.amount ,record.borrow)
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.deleteMoneyRecord(record)
        }
    }

    // レコードを登録した際に借主のsumプロパティの値も更新
    private fun subAmount(currentId: Int, amount: Long, borrow: Boolean ) {
        var borrower = _borrowerList.value?.first { it.id == currentId }
        if (borrower != null) {
            var sum:Long = 0
            if (borrow) {
                sum = borrower.amountSum - amount
            } else {
                sum = borrower.amountSum + amount
            }
            // 借主のsumプロパティの値も更新
            viewModelScope.launch(Dispatchers.IO) {
                rootRepository.updateBorrower(
                    borrower.id,
                    borrower.name,
                    borrower.returnFlag,
                    borrower.current,
                    sum
                )
            }
        }
    }
}
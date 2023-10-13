package com.amefure.loanlist.View.MonerRecords

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoanListViewModel(app:Application):RootViewModel(app) {
    private val _recordList = MutableLiveData<List<MoneyRecord>>()
    val recordList: LiveData<List<MoneyRecord>> = _recordList

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

    fun loadRecordItems(currentId:Int) {
        // データの取得は非同期で
        viewModelScope.launch (Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.loadMoneyRecords(currentId) {
                // 日付の降順でソートをかけておく
                _recordList.postValue(it.sortedBy { it.date }.reversed())  // 本来はDBやCacheから取得
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
package com.amefure.loanlist.View.MonerRecords

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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

    fun registerMoneyRecord(currentId:Int,amount: Long,desc:String,borrow:Boolean,date:String) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertMoneyRecord(currentId,amount,desc,borrow,date)
        }
    }

    fun deleteMoneyRecord(record: MoneyRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.deleteMoneyRecord(record)
        }
    }
}
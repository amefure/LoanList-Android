package com.amefure.loanlist.View.MonerRecords

import android.app.Application
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
    val borrowerList: LiveData<List<MoneyRecord>> = _recordList

    fun loadRecordItems() {
        // データの取得は非同期で
        viewModelScope.launch (Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.loadMoneyrecords {
                _recordList.postValue(it)  // 本来はDBやCacheから取得
            }
        }
    }

    fun registerMoneyRecord(amount: Long,desc:String,borrow:Boolean,date:String) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertMoneyRecord(amount,desc,borrow,date)
        }
    }

    fun deleteMoneyRecord(record: MoneyRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.deleteMoneyRecord(record)
        }
    }
}
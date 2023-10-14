package com.amefure.loanlist.View.Input

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputViewModel(app:Application):RootViewModel(app) {

    // レコードを登録や削除時にsumプロパティの値を変化させるため内部的に保持
    // 外部には公開しないがFragmentのcreate時にloadBorrowerItems
    private val _borrowerList = MutableLiveData<List<Borrower>>()

    public fun loadBorrowerItems() {
        // データの取得は非同期で
        viewModelScope.launch (Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.loadBorrowerItems {
                _borrowerList.postValue(it)  // 本来はDBやCacheから取得
            }
        }
    }

    public fun registerRecord(currentId: Int, amount: Long, desc: String, borrow: Boolean, date: String) {
        addAmount(currentId,amount,borrow)
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertMoneyRecord(currentId,amount,desc,borrow,date)
        }
    }

    public fun updateRecord(currentId: Int, recordId: Int, amount: Long, desc: String, borrow: Boolean, date: String) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.updateMoneyRecord(currentId,recordId,amount,desc,borrow,date)
        }
    }


    // レコードを登録した際に借主のsumプロパティの値も更新
    private fun addAmount(currentId: Int, amount: Long, borrow: Boolean ) {
        var borrower = _borrowerList.value?.first { it.id == currentId }
        if (borrower != null) {
            var sum:Long = 0
            if (borrow) {
                sum = borrower.amountSum + amount
            } else {
                sum = borrower.amountSum - amount
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
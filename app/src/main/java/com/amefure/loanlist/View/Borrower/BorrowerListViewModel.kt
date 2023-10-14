package com.amefure.loanlist.View.Borrower

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class BorrowerListViewModel(app: Application) : RootViewModel(app) {

    private val _borrowerList = MutableLiveData<List<Borrower>>()
    public val borrowerList: LiveData<List<Borrower>> = _borrowerList
    private val dataStoreManager = DataStoreManager(app)

    public fun loadBorrowerItems() {
        // データの取得は非同期で
        viewModelScope.launch (Dispatchers.IO) {  // データ取得はIOスレッドで
            rootRepository.loadBorrowerItems {
                _borrowerList.postValue(it)  // 本来はDBやCacheから取得
            }
        }
    }

    public fun registerBorrower(name:String) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertBorrower(name)
        }
    }

    public fun updateBorrower(id: Int, name:String, returnFlag: Boolean ,current: Boolean, amountSum: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.updateBorrower(id,name,returnFlag,current, amountSum)
        }
    }

    public fun deleteBorrower(borrower:Borrower) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreManager.saveCurrentUserId(0)
            rootRepository.deleteBorrower(borrower)
            // 紐づいているレコードも全て削除
            rootRepository.deleteBorrowerAllMoneyRecord(borrower.id)
        }
    }
}
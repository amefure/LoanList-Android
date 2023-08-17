package com.amefure.loanlist.View.Input

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.RootViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InputViewModel(app:Application):RootViewModel(app) {

    private val _loanList = MutableLiveData<List<MoneyRecord>>()
    val loanList: LiveData<List<MoneyRecord>> = _loanList

    fun registerRecord(currentId:Int,amount: Long, desc:String, borrow:Boolean, date:String) {
        viewModelScope.launch(Dispatchers.IO) {
            rootRepository.insertMoneyRecord(currentId,amount,desc,borrow,date)
        }

    }
}
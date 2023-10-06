package com.amefure.loanlist.Models.Room

import android.content.Context
import androidx.room.Room
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.temporal.TemporalAmount

class RootRepository (context: Context) {

    private val borrowerDao = BorrowerDatabase.getDatabase(context).dao()
    private val recordDao = MoneyRecordDatabase.getDatabase(context).dao()

    private val compositeDisposable = CompositeDisposable()



    public fun loadBorrowerItems(callback: (List<Borrower>) -> Unit) {
        compositeDisposable.add(
            borrowerDao.getAllBorrowers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { borrowers ->
                        // データ取得完了時の処理
                        callback(borrowers)
                    }
                )
        )
    }

    public fun insertBorrower(name:String) {
        val borrower = Borrower(
            id = 0,
            name = name,
            returnFlag = false
        )
            borrowerDao.insertBorrower(borrower)
    }

    public fun deleteBorrower(borrower:Borrower) {
        borrowerDao.deleteBorrower(borrower)
    }


//    MoneyRecords

    public fun loadMoneyRecords(currentId:Int,callback: (List<MoneyRecord>) -> Unit) {
        compositeDisposable.add(
            recordDao.getAllRecordsForBorrower(currentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { records ->
                        // データ取得完了時の処理
                        callback(records)
                    }
                )
        )
    }

    public fun insertMoneyRecord(currentId:Int,amount: Long,desc:String,borrow:Boolean,date:String) {
    val record = MoneyRecord(
        id = 0,
        amount = amount,
        desc = desc,
        borrow = borrow,
        date = date,
        borrowerId = currentId
    )
        recordDao.insertMoneyRecord(record)
    }

    public fun updateMoneyRecord(currentId:Int,recordId:Int,amount: Long,desc:String,borrow:Boolean,date:String) {
        val record = MoneyRecord(
            id = recordId,
            amount = amount,
            desc = desc,
            borrow = borrow,
            date = date,
            borrowerId = currentId
        )
        recordDao.updateMoneyRecord(record)
    }

    public fun deleteMoneyRecord(record:MoneyRecord) {
        recordDao.deleteMoneyRecord(record)
    }


}
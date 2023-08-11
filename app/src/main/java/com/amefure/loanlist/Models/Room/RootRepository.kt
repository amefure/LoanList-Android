package com.amefure.loanlist.Models.Room

import android.content.Context
import androidx.room.Room
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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

    public fun loadMoneyrecords(callback: (List<MoneyRecord>) -> Unit) {
        compositeDisposable.add(
            recordDao.getAllRecordsForBorrower(1)
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

    public fun insertMoneyRecord(amount: Long,desc:String,borrow:Boolean,date:String) {
    val record = MoneyRecord(
        id = 0,
        amount = amount,
        desc = desc,
        borrow = borrow,
        date = date,
        borrowerId = 1
    )
        recordDao.insertMoneyRecord(record)
    }

    public fun deleteMoneyRecord(record:MoneyRecord) {
        recordDao.deleteMoneyRecord(record)
    }

}
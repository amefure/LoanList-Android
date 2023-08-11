package com.amefure.loanlist

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.Models.Room.MoneyRecordDao
import com.amefure.loanlist.Models.Room.MoneyRecordDatabase
import com.amefure.loanlist.View.BorrowerListFragment
import com.amefure.loanlist.View.InputFragment
import com.amefure.loanlist.View.LoanListAdapter
import com.amefure.loanlist.View.SettingsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var db : MoneyRecordDatabase
        lateinit var dao : MoneyRecordDao
    }

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = MoneyRecordDatabase.getDatabase(this)
        dao = db.dao()

        val actionBtn:FloatingActionButton = findViewById(R.id.floating_action_button)
        actionBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, InputFragment())
                addToBackStack(null)
                commit()
            }
        }

        val nameBtn:Button = findViewById(R.id.name_buttnon)
        nameBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, BorrowerListFragment())
                addToBackStack(null)
                commit()
            }
        }

        val navigationBtn:ImageButton = findViewById(R.id.navigation_button)
        navigationBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, SettingsFragment())
                addToBackStack(null)
                commit()
            }
        }
        test()
    }

    fun test(){
        val recyclerView: RecyclerView = findViewById(R.id.loan_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        compositeDisposable.add(
            dao.getAllRecordsForBorrower(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        //データ取得完了時の処理
                        val data = ArrayList<MoneyRecord>()
                        it.forEach {
                                user -> data.add(user)
                        }
                        val adapter = LoanListAdapter(data)
                        recyclerView.adapter = adapter
                    }
                )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}
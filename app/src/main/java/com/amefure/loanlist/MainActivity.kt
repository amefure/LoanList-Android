package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.View.Borrower.BorrowerListFragment
import com.amefure.loanlist.View.Input.InputFragment
import com.amefure.loanlist.View.MonerRecords.LoanListAdapter
import com.amefure.loanlist.View.MonerRecords.LoanListSwipeToDeleteCallback
import com.amefure.loanlist.View.MonerRecords.LoanListViewModel
import com.amefure.loanlist.View.Settings.SettingsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    private val viewModel: LoanListViewModel by viewModels()

    private val dataStoreManager = DataStoreManager(this)

    // アクティブになるBorrowerID
    private var currentId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 選択されているBorrowerの変更を観測する
        observeCurrentBorrowerId()
        observeCurrentBorrowerName()

        val actionBtn: FloatingActionButton = findViewById(R.id.floating_action_button)
        actionBtn.setOnClickListener {
            if (currentId != null) {
                // Borrowerが設定されていれば
                // Input画面へ遷移
                supportFragmentManager.beginTransaction().apply {
                    add(R.id.main_frame, InputFragment())
                    addToBackStack(null)
                    commit()
                }
            } else {
                // Borrowerが設定されていなければ
                // 設定されていないことを警告
                Toast.makeText(this,currentId.toString(),Toast.LENGTH_LONG)
                    .show()
            }
        }

        val nameBtn: Button = findViewById(R.id.name_buttnon)
        nameBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, BorrowerListFragment())
                addToBackStack(null)
                commit()
            }
        }

        val navigationBtn: ImageButton = findViewById(R.id.navigation_button)
        navigationBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, SettingsFragment())
                addToBackStack(null)
                commit()
            }
        }


        if (currentId != null) {
            // アクティブになっているBorrowerがある時のみレコードをセット
            observedMoneyRecordsData(currentId as Int)
        }

    }

    /// Borrowerに紐づくレコードデータをセット&観測
    private fun observedMoneyRecordsData(currentId:Int) {
        val recyclerView: RecyclerView = findViewById(R.id.loan_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        viewModel.recordList.observe(this) {
            val adapter = LoanListAdapter(viewModel, it)
            val swipeToDeleteCallback = LoanListSwipeToDeleteCallback(adapter)
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
        }
        viewModel.loadRecordItems(currentId)
    }

    /// アクティブになっているBorrowerIDを観測し変更があるたびにレコードデータを更新する
    private fun observeCurrentBorrowerId() {
        lifecycleScope.launch{
            dataStoreManager.observeCurrentUserId().collect {
                currentId =  it
                observedMoneyRecordsData(currentId as Int)
            }
        }
    }

    /// アクティブになっているBorrowerNameを観測し変更があるたびにNameButtonを更新する
    private fun observeCurrentBorrowerName() {
        val nameBtn: Button = findViewById(R.id.name_buttnon)
        lifecycleScope.launch{
            dataStoreManager.observeCurrentUserName().collect {
                nameBtn.text =  it.toString()
            }
        }
    }
}
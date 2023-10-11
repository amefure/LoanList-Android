package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.View.Borrower.BorrowerListFragment
import com.amefure.loanlist.View.Input.InputFragment
import com.amefure.loanlist.View.MonerRecords.LoanDetailFragment
import com.amefure.loanlist.View.MonerRecords.LoanListAdapter
import com.amefure.loanlist.View.MonerRecords.LoanListViewModel
import com.amefure.loanlist.View.Settings.SettingsFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() ,LoanDetailFragment.eventListener{
    override fun onDeleteClick(id: Int) {
        // LoanDetailFragmentで削除ボタンを押されたイベント
        adapter.deleteItem(id)
        supportFragmentManager.popBackStack()
    }

    private val viewModel: LoanListViewModel by viewModels()

    private val dataStoreManager = DataStoreManager(this)

    private lateinit var adapter: LoanListAdapter
    private lateinit var recyclerView: RecyclerView

    // アクティブになるBorrowerID
    private var currentId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.loan_list)

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
                Toast.makeText(this,"",Toast.LENGTH_LONG)
                    .show()
            }
        }
        // Borrowerリストページへの画面遷移
        val nameBtn: Button = findViewById(R.id.name_buttnon)
        nameBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, BorrowerListFragment())
                addToBackStack(null)
                commit()
            }
        }

        // 設定ページへの画面遷移
        val navigationBtn: ImageButton = findViewById(R.id.navigation_button)
        navigationBtn.setOnClickListener {
            supportFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, SettingsFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    /// Borrowerに紐づくレコードデータをセット&観測
    private fun observedMoneyRecordsData(currentId:Int) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
        // 先に対象借主のリストを読み込む
        viewModel.loadRecordItems(currentId as Int)

        // 観測開始
        viewModel.recordList.observe(this) {
            adapter = LoanListAdapter(viewModel, it)

            adapter.setOnBookCellClickListener(
                object : LoanListAdapter.OnBookCellClickListener {
                    override fun onItemClick(record: MoneyRecord) {

                        val fragment = LoanDetailFragment.newInstance(
                            record.id,
                            record.amount.toString(),
                            record.date,
                            record.borrow,
                            record.desc
                        )
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.main_frame, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                }
            )
            recyclerView.adapter = adapter
        }
    }

    /// アクティブになっているBorrowerIDを観測し変更があるたびにレコードデータを更新する
    private fun observeCurrentBorrowerId() {
        lifecycleScope.launch{
            dataStoreManager.observeCurrentUserId().collect {
                currentId = it
                //　削除された際(IDが0)にはnullにする
                if (currentId == 0) {
                    currentId = null
                }
                // nullじゃなければ該当Borrowerのレコードを観測開始
                if (currentId != null) {
                    observedMoneyRecordsData(currentId as Int)
                }
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
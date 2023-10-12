package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
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
import kotlin.math.abs

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
    private var amountMarkFlag: Boolean = true
    private var sumAmount: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.loan_list)

        viewModel.loadBorrowerItems()

        // 選択されているBorrowerの変更を観測する
        observeCurrentBorrowerId()
        observeCurrentBorrower()
        observeAmountMark()

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

    /// ローカルに保存されている
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

    // ローカルに保存されているアクティブIDを元に
    // 借主情報をの変更を観測&UI反映
    private fun observeCurrentBorrower() {
        viewModel.borrowerList.observe(this) {
            val sumAmountLabel: TextView = findViewById(R.id.sum_amount_label)
            val nameBtn: Button = findViewById(R.id.name_buttnon)

            if (it.size != 0 && currentId != null) {

                // 合計金額をセット
                sumAmount = it.first { it.id == currentId }.amountSum
                // ラベルを更新
                setJudgeSumAmountLabel(sumAmount)

                // 名前をセット
                val name = it.first { it.id == currentId }.name
                nameBtn.setText(name)
            } else {
                sumAmountLabel.setText("0円")
                nameBtn.setText("unknown")
                // レコードの削除はBorrower削除時に実行ずみ
            }
        }
    }

    // 設定画面から+/-の変更を押されたことを検知する
    private fun observeAmountMark() {
        lifecycleScope.launch{
            dataStoreManager.observeAmountMark().collect {
                if (it == null || it == "借") {
                    amountMarkFlag = true
                } else {
                    amountMarkFlag = false
                }
                setJudgeSumAmountLabel(sumAmount)
            }
        }
    }

    // レコードの合計金額ラベルをセットする
    private fun setJudgeSumAmountLabel(result: Long) {
        val sumAmountLabel: TextView = findViewById(R.id.sum_amount_label)
        if (result < 0) {
            // マイナス値
            if (amountMarkFlag) {
                sumAmountLabel.setText("-" + "%,d".format(abs(result)) + "円")
            } else {
                sumAmountLabel.setText("+" + "%,d".format(abs(result)) + "円")
            }
        } else {
            // プラス値
            if (amountMarkFlag) {
                sumAmountLabel.setText("+" + "%,d".format(result) + "円")
            } else {
                sumAmountLabel.setText("-" + "%,d".format(result) + "円")
            }
        }
    }
}
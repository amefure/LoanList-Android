package com.amefure.loanlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.DataStore.SortItem
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
    // 設定画面のスイッチで変更されるフラグ
    private var amountMarkFlag: Boolean = true
    // レコードの合計金額保持用
    private var sumAmount: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.loan_list)

        // 借主一覧を取得
        // IDはローカルに保存しているが名前と合計は保存していないのでDBから取得する
        viewModel.loadBorrowerItems()

        // 選択されているBorrowerの変更を観測する(ローカル)
        observeCurrentBorrowerId()
        // 借主情報の変更を観測&UI反映
        observeCurrentBorrower()
        // 設定画面のスイッチの値を観測
        observeAmountMark()
        // SortItemを観測
        observeSortItem()

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

        val sortBtn:ImageButton = findViewById(R.id.sort_button)
        val spinner:Spinner = findViewById(R.id.sort_spinner)
        spinner.visibility = View.GONE
        sortBtn.setOnClickListener {
            spinner.visibility = View.VISIBLE
            sortBtn.visibility = View.GONE
        }

        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        SortItem.values().forEach {
            spinnerAdapter.add(it.message())
        }
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = spinnerAdapterListener

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

    // ローカルに保存されている
    // アクティブになっているBorrowerIDを観測し変更があるたびにレコードデータを更新する
    // 借主リスト画面で新しく選択された時に発火
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
    // 借主情報の変更を観測&UI反映
    // 借主リスト画面で新しく選択された時に発火
    private fun observeCurrentBorrower() {
        viewModel.borrowerList.observe(this) {
            val sumAmountLabel: TextView = findViewById(R.id.sum_amount_label)
            val nameBtn: Button = findViewById(R.id.name_buttnon)

            val dataStoreManager = DataStoreManager(this)

            if (it.size != 0 && currentId != null) {
                var currentBorrower = it.first { it.id == currentId }

                // 合計金額をセット
                sumAmount = currentBorrower.amountSum
                // ラベルを更新
                setJudgeSumAmountLabel(sumAmount)

                // 名前をセット
                val name = currentBorrower.name
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

    private fun observeSortItem() {
        lifecycleScope.launch {
            dataStoreManager.observeSortItem().collect {
                val spinner:Spinner = findViewById(R.id.sort_spinner)
                when (it) {
                    SortItem.AMOUNT_ASCE.name -> {
                        spinner.setSelection(0)
                    }
                    SortItem.AMOUNT_DESC.name  -> {
                        spinner.setSelection(1)
                    }
                    SortItem.DATE_ASCE.name  -> {
                        spinner.setSelection(2)
                    }
                    SortItem.DATE_DESC.name  -> {
                        spinner.setSelection(3)
                    }
                    SortItem.BORROW_ONLY.name  -> {
                        spinner.setSelection(4)
                    }
                    SortItem.LOAN_ONLY.name  -> {
                        spinner.setSelection(5)
                    }
                    else -> {
                        spinner.setSelection(5)
                    }
                }
            }
        }
    }

    // スピナーがタップ
    private val spinnerAdapterListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            // 選択された時に実行したい処理
            var sortClearbtn: ImageButton = findViewById(R.id.sort_clear_button)
            lifecycleScope.launch {
                dataStoreManager.saveSortItem(SortItem.values()[position].name)
                sortClearbtn.imageTintList = ContextCompat.getColorStateList(view!!.context, R.color.thema1)

            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
}



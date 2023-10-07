package com.amefure.loanlist.View.MonerRecords

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.R
import com.amefure.loanlist.View.Borrower.BorrowerListFragment
import com.amefure.loanlist.View.Borrower.BorrowerListViewModel


class LoanListAdapter(private val viewModel: LoanListViewModel, recordList: List<MoneyRecord>) :RecyclerView.Adapter<LoanListAdapter.MainViewHolder>(){

    private val _recordList: MutableList<MoneyRecord> = recordList.toMutableList()
    override fun getItemCount(): Int = _recordList.size

    private lateinit var listener: OnBookCellClickListener

    // 2. インターフェースを作成
    interface OnBookCellClickListener {
        fun onItemClick(record: MoneyRecord)
    }

    // 3. リスナーをセット
    fun setOnBookCellClickListener(listener: OnBookCellClickListener) {
        // 定義した変数listenerに実行したい処理を引数で渡す（BookListFragmentで渡している）
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_loan_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {

        val record = _recordList[position]

        holder.itemView.setOnClickListener {
            // セルがクリックされた時にインターフェースの処理が実行される
            listener.onItemClick(record)
        }

        holder.amount.text = "%,d".format(record.amount) + "円"
        holder.date.text = record.date
        holder.memo.text = record.desc
        if (record.borrow) {
            holder.backImg.setImageResource(R.drawable.loan_list_item_background)
        } else {
            holder.backImg.setImageResource(R.drawable.loan_list_item_background2)
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amount: TextView = itemView.findViewById(R.id.amount_text)
        val date: TextView = itemView.findViewById(R.id.date_text)
        val memo: TextView = itemView.findViewById(R.id.memo_text)
        val backImg: ImageView = itemView.findViewById(R.id.back_image)
    }
    public fun deleteItem(id: Int) {
        var item = _recordList.filter { it.id == id }
        viewModel.deleteMoneyRecord(item.first())
    }
}
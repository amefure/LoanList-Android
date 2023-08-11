package com.amefure.loanlist.View.MonerRecords

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.R
import com.amefure.loanlist.View.Borrower.BorrowerListViewModel


class LoanListAdapter(private val viewModel: LoanListViewModel, recordList: List<MoneyRecord>) :RecyclerView.Adapter<LoanListAdapter.MainViewHolder>(){

    private val _recordList: MutableList<MoneyRecord> = recordList.toMutableList()

    override fun getItemCount(): Int = _recordList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_loan_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val record = _recordList[position]

        holder.amount.text = record.amount.toString()
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
    fun deleteItem(position: Int) {
        if (position < 0 || position >= _recordList.size) {
            return
        }
        val item = _recordList[position]
        viewModel.deleteMoneyRecord(item)
        notifyItemRemoved(position)
    }
}
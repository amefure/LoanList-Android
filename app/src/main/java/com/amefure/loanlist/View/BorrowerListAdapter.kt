package com.amefure.loanlist.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.emptyPreferences
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.DataStore.dataStore
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException

class BorrowerListAdapter (borrowerList: List<Borrower>) :RecyclerView.Adapter<BorrowerListAdapter.MainViewHolder>(){

    private val _borrowerList: MutableList<Borrower> = borrowerList.toMutableList()

    override fun getItemCount(): Int = _borrowerList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            // XMLレイアウトファイルからViewオブジェクトを作成
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_borrower_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val borrower = _borrowerList[position]

        holder.borrower.text = borrower.name
        holder.amount.text = "00"
        if (borrower.returnFlag) {
            holder.returnFlagImage.setImageResource(R.drawable.user_flag)
        } else {
            holder.returnFlagImage.setImageResource(R.drawable.user_done_flag)
        }
        if (holder.getCurrentBorrower() == borrower.name) {
            holder.activeFlagImage.setImageResource(R.drawable.check_button)
        } else {
            holder.activeFlagImage.visibility = View.GONE
        }
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val borrower:TextView = itemView.findViewById(R.id.borrower_name)
        val amount:TextView = itemView.findViewById(R.id.sum_amount)
        val returnFlagImage:ImageView = itemView.findViewById(R.id.return_flag_img)
        val activeFlagImage:ImageView = itemView.findViewById(R.id.active_flag_img)

        val dataStoreManager = DataStoreManager(itemView.context)

        fun getCurrentBorrower(): String{
            var name = ""
            runBlocking {
                val flow = dataStoreManager.observeCurrentUser()
                name = flow.first().toString()
            }
            return name
        }
    }
}
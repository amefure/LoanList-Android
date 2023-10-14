package com.amefure.loanlist.View.Borrower

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// リサイクルビュー用リストアダプター
class BorrowerListAdapter (private val viewModel: BorrowerListViewModel, borrowerList: List<Borrower>) :RecyclerView.Adapter<BorrowerListAdapter.MainViewHolder>(){
    private var _borrowerList: MutableList<Borrower> = borrowerList.toMutableList()

    override fun getItemCount(): Int = _borrowerList.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_borrower_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val borrower = _borrowerList[position]
        holder.borrower.text = borrower.name
        holder.amount.text = "%,d".format(borrower.amountSum) + "円"
        if (borrower.returnFlag) {
            holder.returnFlagImage.setImageResource(R.drawable.user_check_flag)
        } else {
            holder.returnFlagImage.setImageResource(R.drawable.user_flag)
        }
        if (holder.getCurrentBorrowerId() == borrower.id) {
            holder.activeFlagImage.setImageResource(R.drawable.check_button)
        } else {
            holder.activeFlagImage.visibility = View.GONE
        }
    }

    public  fun getItemAtPosition(position: Int) : Borrower? {
        if (position < 0 || position >= _borrowerList.size) {
            return null
        }
        val item = _borrowerList[position]
        return item
    }

    // Borrowerがスワイプされた時にUIが残留しないように明示的に更新するための処理
     public fun notifyItemUpdateView(position: Int) {
        if (position < 0 || position >= _borrowerList.size) {
            return
        }
        val item = _borrowerList[position]
        viewModel.updateBorrower(item.id,item.name,item.returnFlag,item.current, item.amountSum)
        notifyItemChanged(position)
    }

    // リサイクルビューから返済フラグを更新
    public fun updateItem(position: Int){
        if (position < 0 || position >= _borrowerList.size) {
            return
        }
        var item = _borrowerList[position]
        // 明示的に_borrowerListを更新しないとUIがスワイプのUIが変化しない
        item.returnFlag = !item.returnFlag
        _borrowerList[position] = item
        viewModel.updateBorrower(item.id, item.name, item.returnFlag, item.current, item.amountSum)
        notifyItemChanged(position)
    }

    public fun updateItem2(position: Int , name:String, returnFlag: Boolean , current: Boolean, amountSum: Long){
        if (position < 0 || position >= _borrowerList.size) {
            return
        }
        val item = _borrowerList[position]
        viewModel.updateBorrower(item.id, name, !returnFlag, current, amountSum)
        notifyItemChanged(position)
    }
    public fun deleteItem(position: Int) {
        if (position < 0 || position >= _borrowerList.size) {
            return
        }
        val item = _borrowerList[position]
        viewModel.deleteBorrower(item)
        notifyItemRemoved(position)
    }

    class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val borrower:TextView = itemView.findViewById(R.id.borrower_name)
        val amount:TextView = itemView.findViewById(R.id.sum_amount)
        val returnFlagImage:ImageView = itemView.findViewById(R.id.return_flag_img)
        val activeFlagImage:ImageView = itemView.findViewById(R.id.active_flag_img)

        val dataStoreManager = DataStoreManager(itemView.context)

        fun getCurrentBorrowerId(): Int?{
            var id:Int?
            runBlocking {
                val flow = dataStoreManager.observeCurrentUserId()
                id = flow.first()
            }
            return id
        }
    }
}
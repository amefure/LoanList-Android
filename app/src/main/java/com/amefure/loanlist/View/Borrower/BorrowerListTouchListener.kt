package com.amefure.loanlist.View.Borrower

import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.Models.Room.Borrower
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking

class BorrowerListTouchListener(private val viewModel: BorrowerListViewModel) : RecyclerView.SimpleOnItemTouchListener() {
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        // タッチイベントの処理
        if (e.action == MotionEvent.ACTION_DOWN) {
            // タッチされた位置のViewを取得
            val childView: View? = rv.findChildViewUnder(e.x, e.y)
            if (childView != null) {
                val position = rv.getChildAdapterPosition(childView)
                if (position != RecyclerView.NO_POSITION) {
                    val adapter = rv.adapter
                    if (adapter is BorrowerListAdapter) {
                        val tappedItem: Borrower? = adapter.getItemAtPosition(position)
                        if (tappedItem != null) {
                            val dataStoreManager = DataStoreManager(rv.context)
                            runBlocking {
                                // タッチされたアイテムをアクティブにする
                                dataStoreManager.saveCurrentUserId(tappedItem.id.toInt())
                                // データを変更することでRecyclerViewも更新される
                                viewModel.updateBorrower(
                                    tappedItem.id,
                                    tappedItem.name,
                                    tappedItem.returnFlag,
                                    true,
                                    tappedItem.amountSum)
                            }
                        }
                    }
                }
            }
        }
        return false // 通常のタッチイベント処理を維持
    }
}

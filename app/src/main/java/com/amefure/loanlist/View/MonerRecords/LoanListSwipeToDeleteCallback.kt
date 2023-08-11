package com.amefure.loanlist.View.MonerRecords

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.R
import com.amefure.loanlist.View.MonerRecords.LoanListAdapter

class LoanListSwipeToDeleteCallback (private val adapter: LoanListAdapter) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE, // ドラッグアンドドロップをサポートしない場合は0
    ItemTouchHelper.LEFT // スワイプの方向を指定
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // ドラッグアンドドロップの処理を実装
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // スワイプしたアイテムを削除する処理を実装
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }

    // スワイプ時の背景色とアイコンを描画
    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val background: ColorDrawable = ColorDrawable(Color.RED)

        // スワイプ時のアイコンを定義AppCompatResources
        val deleteIcon: Drawable? = AppCompatResources.getDrawable(recyclerView.context, R.drawable.delete)

        if (deleteIcon != null) {

            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon.intrinsicHeight

            when {
                dX < 0 -> { // 左方向へのスワイプ
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                }
                else -> {
                    background.setBounds(0, 0, 0, 0)
                }
            }

            background.draw(canvas)
            deleteIcon.draw(canvas)
        }
    }
}



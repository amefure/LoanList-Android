package com.amefure.loanlist.View.Borrower


import android.app.AlertDialog
import android.content.ClipData.Item
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.R
import com.amefure.loanlist.View.Borrower.BorrowerListAdapter
import kotlin.math.log

class BorrowerListSwipeToDeleteCallback(private val adapter: BorrowerListAdapter) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.ACTION_STATE_IDLE, // ドラッグアンドドロップをサポートしない場合は0
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // スワイプの方向を指定
) {

    private var context: Context? = null

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        // ドラッグアンドドロップの処理を実装
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        if (direction == ItemTouchHelper.LEFT) {
            // ダイアログの表示
            AlertDialog.Builder(context)
                .setTitle("削除しますか？")
                .setPositiveButton("OK", { dialog, which ->
                    /// スワイプしたアイテムを削除する処理を実装
                    val position = viewHolder.adapterPosition
                    adapter.deleteItem(position)
                })
                .setNegativeButton("キャンセル", { dialog, which ->
                    // キャンセル
                    val position = viewHolder.adapterPosition
                    // Viewのみ更新する
                    adapter.notifyItemUpdateView(position)
                })
                .show()

        } else if (direction == ItemTouchHelper.RIGHT) {
            val position = viewHolder.adapterPosition
            adapter.updateItem(position)
        }
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

        context = recyclerView.context
        // 右スワイプ
        // Delete用背景色
        val deleteBackGround: ColorDrawable = ColorDrawable(Color.RED)

        // 左スワイプ
        // 返却用用背景色
        val colorValue = ContextCompat.getColor(recyclerView.context, R.color.thema1)
        val returnBackGround: ColorDrawable = ColorDrawable(colorValue)

        val colorValue2 = ContextCompat.getColor(recyclerView.context, R.color.thema6)
        val returnBackGround2: ColorDrawable = ColorDrawable(colorValue2)


        // スワイプ時のアイコンを定義AppCompatResources
        val deleteIcon: Drawable? = AppCompatResources.getDrawable(recyclerView.context,R.drawable.delete)

        if (deleteIcon != null) {

            val itemView = viewHolder.itemView
            val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
            val iconBottom = iconTop + deleteIcon.intrinsicHeight

            when {
                dX > 0 -> { // 右方向へのスワイプ

                    var item = adapter.getItemAtPosition(viewHolder.adapterPosition)
                    var text = ""
                    if (item != null) {
                        if (item.returnFlag) {
                            text = "未返済にする"
                            returnBackGround2.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                            returnBackGround2.draw(canvas)
                        } else {
                            text = "返済済みにする"
                            returnBackGround.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                            returnBackGround.draw(canvas)
                        }
                    }

                    val paint = Paint()
                    paint.color = Color.WHITE
                    paint.textSize = 40f
                    val textLeft = itemView.left + iconMargin
                    val centerY = itemView.top + itemView.height / 2
                    val textHeight = (paint.descent() + paint.ascent()) / 2
                    val textY = centerY - textHeight
                    canvas.drawText(text, textLeft.toFloat(), textY, paint)
                }
                dX < 0 -> { // 左方向へのスワイプ
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteBackGround.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteBackGround.draw(canvas)
                    deleteIcon.draw(canvas)
                }
                else -> {
                    deleteBackGround.setBounds(0, 0, 0, 0)
                    deleteBackGround.draw(canvas)
                }
            }


        }
    }
}



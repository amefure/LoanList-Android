package com.amefure.loanlist.View.MonerRecords

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.amefure.loanlist.R
import com.amefure.loanlist.View.Input.InputFragment

private const val ARG_ID_KEY = "ARG_ID_KEY"
private const val ARG_AMOUNT_KEY = "ARG_AMOUNT_KEY"
private const val ARG_DATE_KEY = "ARG_DATE_KEY"
private const val ARG_IS_LOAN_KEY = "ARG_IS_LOAN_KEY"
private const val ARG_MEMO_KEY = "ARG_MEMO_KEY"

class LoanDetailFragment : Fragment() {

    private var id: Int = 0
    private var amount: String = ""
    private var date: String = ""
    private var borrow:Boolean = true
    private var memo:String = ""

    // リスナーの定義
    private lateinit var listener: eventListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            id = it.getInt(ARG_ID_KEY,0)
            amount = it.getString(ARG_AMOUNT_KEY,"0")
            date = it.getString(ARG_DATE_KEY,"2023/10/1")
            borrow = it.getBoolean(ARG_IS_LOAN_KEY,true)
            memo = it.getString(ARG_MEMO_KEY,"")
        }
        return inflater.inflate(R.layout.fragment_loan_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val amountLabel:TextView = view.findViewById(R.id.amount_label)
        val dateLabel:TextView = view.findViewById(R.id.date_label)
        val backImg:ImageView = view.findViewById(R.id.back_image)
        val memoLabel:TextView = view.findViewById(R.id.memo_text)
        amountLabel.setText(amount + "円")
        dateLabel.setText(date)
        memoLabel.setText(memo)

        if (borrow) {
            backImg.setImageResource(R.drawable.loan_list_item_background)
        } else {
            backImg.setImageResource(R.drawable.loan_list_item_background2)
        }
        val editBtn:ImageButton = view.findViewById(R.id.edit_button)
        editBtn.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                add(R.id.main_frame, InputFragment.editInstance(id,amount,date,borrow,memo))
                addToBackStack(null)
                commit()
            }
        }

        val deleteBtn:ImageButton = view.findViewById(R.id.delete_button)
        deleteBtn.setOnClickListener {

            // ダイアログの表示
            AlertDialog.Builder(this.requireContext())
                .setTitle("削除しました。")
                .setPositiveButton("OK", { dialog, which ->
                    // MainActivityへイベントを送信
                    listener?.onDeleteClick(id)
                })
                .show()

        }
    }


    // リスナーの格納
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as eventListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnboardSignUpTermsOfServiceListener")
        }
    }

    // イベントリスナー(MainActivityへDeleteボタンタップイベントを送付)
    interface eventListener {
        fun onDeleteClick(id: Int)
    }

    companion object{
        @JvmStatic
        fun newInstance(id: Int, amount: String, date: String, borrow: Boolean, memo: String) =
            LoanDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID_KEY, id)
                    putString(ARG_AMOUNT_KEY, amount)
                    putString(ARG_DATE_KEY, date)
                    putBoolean(ARG_IS_LOAN_KEY, borrow)
                    putString(ARG_MEMO_KEY,memo)
                }
            }
    }
}
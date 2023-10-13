package com.amefure.loanlist.View.Input

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar

private const val ARG_ID_KEY = "ARG_ID_KEY"
private const val ARG_AMOUNT_KEY = "ARG_AMOUNT_KEY"
private const val ARG_DATE_KEY = "ARG_DATE_KEY"
private const val ARG_IS_LOAN_KEY = "ARG_IS_LOAN_KEY"
private const val ARG_MEMO_KEY = "ARG_MEMO_KEY"
class InputFragment : Fragment() {

    private val viewModel:InputViewModel by viewModels()

    // LoanDetail画面からの遷移した場合に格納される(編集)
    private var receiveId: Int? = null
    private var receiveAmount: String = ""
    private var receiveDate: String = ""
    private var receiveBorrow:Boolean = true
    private var receiveMemo:String = ""

    // 入力保持
    private var isBorrow: Boolean = true
    // 日付情報を保持
    private var dateString: String = ""


    private lateinit var amountText:EditText
    private lateinit var memoText:EditText
    private lateinit var datePicker:DatePicker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            receiveId = it.getInt(ARG_ID_KEY,0)
            receiveAmount = it.getString(ARG_AMOUNT_KEY,"0")
            receiveDate = it.getString(ARG_DATE_KEY,"2023/10/1")
            receiveBorrow = it.getBoolean(ARG_IS_LOAN_KEY,true)
            receiveMemo = it.getString(ARG_MEMO_KEY,"")
        }
        return inflater.inflate(R.layout.fragment_input, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadBorrowerItems()

        val currentId = getCurrentBorrowerId()

        val loanButton:Button = view.findViewById(R.id.loan_button)
        val borrowButton:Button = view.findViewById(R.id.borrow_button)
        val doneButton:ImageButton = view.findViewById(R.id.done_button)
        amountText = view.findViewById(R.id.amount_text)
        memoText = view.findViewById(R.id.memo_text)
        datePicker = view.findViewById(R.id.date_picker)

        // 現在の日付情報を取得してセット
        dateString = getNowDate()

        if (receiveId != null) {
            // 編集モードで来たならUIの初期値をセット
            setReceiveUIText()
        }

        borrowButton.setOnClickListener {
            isBorrow = true

            amountText.backgroundTintList =
                ContextCompat.getColorStateList(this.requireContext(), R.color.thema1)
        }
        loanButton.setOnClickListener {
            isBorrow = false
            amountText.backgroundTintList =
                ContextCompat.getColorStateList(this.requireContext(), R.color.thema2)
        }

        // DatePickerの値が変化したときに呼び出されるリスナーを設定
        datePicker.setOnDateChangedListener { datePicker, year, month, day ->
            dateString = getFormatDateString(year,month,day)
        }

        doneButton.setOnClickListener {
            val amount = amountText.text.toString().toLongOrNull()
            val memo =  memoText.text.toString()
            if (amount != null) {
                if (receiveId == null) {
                    // 編集モードではない
                    if (currentId != null) {
                        viewModel.registerRecord(currentId,amount,memo,isBorrow,dateString)
                        showOffKeyboard()
                        Snackbar.make(view,"追加しました。", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.GREEN)
                            .show()
                        parentFragmentManager.apply {
                            popBackStack()
                        }
                    }
                } else {
                    // 編集モード
                    if (currentId != null) {
                        viewModel.updateRecord(currentId,receiveId!!,amount,memo,isBorrow,dateString)
                        showOffKeyboard()
                        Snackbar.make(view,"更新しました。", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(Color.GREEN)
                            .show()
                        parentFragmentManager.apply {
                            // トップまで戻す
                            popBackStack()
                            popBackStack()
                        }
                    }
                }
            } else {
                showOffKeyboard()
                Snackbar.make(view,"金額が未入力です。", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show()
            }
        }
    }


    // キーボードを明示的に閉じる
    private fun setReceiveUIText() {
        amountText.setText(receiveAmount)
        memoText.setText(receiveMemo)
        isBorrow = receiveBorrow
        if (isBorrow) {
            val colorValue = ContextCompat.getColor(requireContext(), R.color.thema1)
            amountText.setBackgroundColor(colorValue)
        } else {
            val colorValue = ContextCompat.getColor(requireContext(), R.color.thema2)
            amountText.setBackgroundColor(colorValue)
        }
        var array = receiveDate.split("-")
        datePicker.updateDate(array[0].toInt(),array[1].toInt() - 1 ,array[2].toInt());
    }

    /// キーボードを明示的に閉じる
    private fun showOffKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }


    // 日付コンポーネントを受け取りString型に変換する
    private fun getNowDate(): String {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return getFormatDateString(year,month,day)
    }
    // 日付コンポーネントを受け取りString型に変換する
    private fun getFormatDateString(year:Int,month:Int,day:Int):String {
        val result = String.format("%04d-%02d-%02d", year, month + 1, day)
        return result
    }

    private fun getCurrentBorrowerId(): Int? {
        val dataStoreManager = DataStoreManager(this.requireContext())
        var id:Int?
        runBlocking {
            val flow = dataStoreManager.observeCurrentUserId()
            id = flow.first().toString().toInt()
        }
        return id
    }

    companion object {
        @JvmStatic
        fun editInstance(id: Int, amount: String, date: String, borrow: Boolean, memo: String) =
            InputFragment().apply {
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

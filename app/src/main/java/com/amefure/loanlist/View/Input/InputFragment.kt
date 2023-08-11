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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.amefure.loanlist.R
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class InputFragment : Fragment() {


    private val viewModel:InputViewModel by viewModels()

    private var isBorrow: Boolean = false
    private var date: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loanButton:Button = view.findViewById(R.id.loan_button)
        val borrowButton:Button = view.findViewById(R.id.borrow_button)
        val doneButton:ImageButton = view.findViewById(R.id.done_button)
        val amountText:EditText = view.findViewById(R.id.amount_text)
        val memoText:EditText = view.findViewById(R.id.memo_text)
        val datePicker:DatePicker = view.findViewById(R.id.date_picker)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        date = getFormatDateString(year,month,day)

        borrowButton.setOnClickListener {
            isBorrow = true
            val colorValue = ContextCompat.getColor(requireContext(), R.color.thema1)
            amountText.setBackgroundColor(colorValue)
        }
        loanButton.setOnClickListener {
            isBorrow = false
            val colorValue = ContextCompat.getColor(requireContext(), R.color.thema2)
            amountText.setBackgroundColor(colorValue)
        }

        // DatePickerの値が変化したときに呼び出されるリスナーを設定
        datePicker.setOnDateChangedListener { datePicker, year, month, day ->
            date = getFormatDateString(year,month,day)
        }
        doneButton.setOnClickListener {

            val amount = amountText.text.toString().toLongOrNull()
            val memo =  memoText.text.toString()
            if (amount != null) {
                viewModel.registerRecord(amount,memo,isBorrow,date)
                showOffKeyboard()
                Snackbar.make(view,"追加しました。", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.GREEN)
                    .show()
                parentFragmentManager.apply {
                    popBackStack()
                }
            } else {
                showOffKeyboard()
                Snackbar.make(view,"金額が未入力です。", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.RED)
                    .show()
            }

        }
    }

    private fun showOffKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun getFormatDateString(year:Int,month:Int,day:Int):String {
        val dateString = String.format("%04d-%02d-%02d", year, month + 1, day)
        return dateString
    }
}

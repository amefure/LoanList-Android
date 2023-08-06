package com.amefure.loanlist.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import com.amefure.loanlist.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class InputFragment : Fragment() {

    private var isLoan: Boolean = false
    private var date: LocalDate? = null

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
        val doneButton:Button = view.findViewById(R.id.done_button)
        val amountText:TextView = view.findViewById(R.id.amount_text)
        val memoText:TextView = view.findViewById(R.id.memo_text)

        val datePicker:DatePicker = view.findViewById(R.id.date_picker)


        loanButton.setOnClickListener {
            isLoan = true
        }
        borrowButton.setOnClickListener {
            isLoan = false
        }
//
        // DatePickerの値が変化したときに呼び出されるリスナーを設定
        datePicker.setOnDateChangedListener { datePicker, year, month, day ->

            val dateString = String.format("%04d-%02d-%02d", year, month + 1, day)
            date = getFormatDate(dateString)
        }
//
        doneButton.setOnClickListener {

            val amount = amountText.text
            val memo =  memoText.text
            parentFragmentManager.apply {
                popBackStack()
            }
        }

    }

    fun getFormatDate(dateStr: String) : LocalDate {
        val df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
        val fdate = LocalDate.parse(dateStr, df)
        return fdate
    }
}

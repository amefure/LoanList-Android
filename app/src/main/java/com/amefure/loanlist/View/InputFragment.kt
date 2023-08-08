package com.amefure.loanlist.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.Models.Room.BorrowerDao
import com.amefure.loanlist.Models.Room.BorrowerDatabase
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.Models.Room.MoneyRecordDao
import com.amefure.loanlist.Models.Room.MoneyRecordDatabase
import com.amefure.loanlist.R
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

class InputFragment : Fragment() {

    companion object {
        lateinit var db : MoneyRecordDatabase
        lateinit var dao : MoneyRecordDao
    }

    private val compositeDisposable = CompositeDisposable()

    private var isLoan: Boolean = false
    private var date: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_input, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = MoneyRecordDatabase.getDatabase(view.context)
        dao = db.dao()


        val loanButton:Button = view.findViewById(R.id.loan_button)
        val borrowButton:Button = view.findViewById(R.id.borrow_button)
        val doneButton:ImageButton = view.findViewById(R.id.done_button)
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
            date = dateString
        }
        doneButton.setOnClickListener {

            val amount = amountText.text
            val memo =  memoText.text


            runBlocking {
                val record = MoneyRecord(
                    id = 0,
                    amount = 0,
                    desc = "Test",
                    borrow = true,
                    date = "2020",
                    borrowerId = 1
                )
                dao.insertMoneyRecord(record)
            }
            parentFragmentManager.apply {
                popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}

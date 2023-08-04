package com.amefure.loanlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amefure.loanlist.databinding.FragmentInputBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date

class InputFragment : Fragment() {

    private lateinit var binding: FragmentInputBinding
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



        binding.loanButton.setOnClickListener {
            isLoan = true
        }
        binding.borrowButton.setOnClickListener {
            isLoan = false
        }

        // DatePickerの値が変化したときに呼び出されるリスナーを設定
        binding.datePicker.setOnDateChangedListener { datePicker, year, month, day ->

            val dateString = String.format("%04d-%02d-%02d", year, month + 1, day)
            date = getFormatDate(dateString)
        }

        binding.doneButton.setOnClickListener {

            val amount = binding.amountText.text
            val memo = binding.memoText.text
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

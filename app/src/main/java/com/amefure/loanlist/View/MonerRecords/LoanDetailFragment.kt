package com.amefure.loanlist.View.MonerRecords

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amefure.loanlist.R
private const val ARG_KEY = "key"
class LoanDetailFragment : Fragment() {

    private var text:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            text = it.getString(ARG_KEY,"初期値")
        }
        return inflater.inflate(R.layout.fragment_loan_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val textView:TextView = view.findViewById(R.id.amount_label)
        textView.setText(text)
    }

    companion object{
        @JvmStatic
        fun newInstance(text:String) =
            LoanDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_KEY,text)
                }
            }
    }
}
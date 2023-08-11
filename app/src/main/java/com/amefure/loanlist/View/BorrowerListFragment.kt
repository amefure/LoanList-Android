package com.amefure.loanlist.View

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.R
import com.google.android.material.snackbar.Snackbar


class BorrowerListFragment : Fragment() {
    private val viewModel: BorrowerListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrower_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val registerButton:Button = view.findViewById(R.id.register_button)
        registerButton.setOnClickListener {
            tappedRegisterButton(view)
        }
        observedBorrowerData(view)
    }

    private fun tappedRegisterButton(view: View,) {
        val nameText:EditText = view.findViewById(R.id.user_name_edit)
        if (!nameText.text.isEmpty()) {
            viewModel.registerBorrower(nameText.text.toString())
            showOffKeyboard()
            Snackbar.make(view,"追加しました。",Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.GREEN)
                .show()
            nameText.text.clear()
        } else {
            Snackbar.make(view,"名前が未入力です。",Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .show()
        }
    }

    private fun showOffKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    // Borrowerデータの観測開始 & RecyclerViewとの紐付け
    private fun observedBorrowerData(view: View){
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        )
        viewModel.borrowerList.observe(this.requireActivity()) {
            val adapter = BorrowerListAdapter(viewModel,it)
            val swipeToDeleteCallback = BorrowerListSwipeToDeleteCallback(adapter)
            val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)
            recyclerView.adapter = adapter
        }
        viewModel.loadBorrowerItems()

    }



}
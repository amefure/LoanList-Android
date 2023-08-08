package com.amefure.loanlist.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amefure.loanlist.Models.Room.Borrower
import com.amefure.loanlist.Models.Room.BorrowerDao
import com.amefure.loanlist.Models.Room.BorrowerDatabase
import com.amefure.loanlist.Models.Room.MoneyRecord
import com.amefure.loanlist.Models.Room.MoneyRecordDao
import com.amefure.loanlist.Models.Room.MoneyRecordDatabase
import com.amefure.loanlist.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.runBlocking


class BorrowerListFragment : Fragment() {

    companion object {
        lateinit var bdb : BorrowerDatabase
        lateinit var bdao : BorrowerDao
    }

    private val compositeDisposable = CompositeDisposable()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_borrower_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bdb = BorrowerDatabase.getDatabase(view.context)
        bdao = bdb.dao()

        val button:Button = view.findViewById(R.id.register_button)
        button.setOnClickListener {
            val borrower = Borrower(
                id = 0,
                name = "Test",
                returnFlag = false
            )

            runBlocking {
                val borrowerId:Int = bdao.insertBorrower(borrower).toInt()
            }
        }
        test(view)
    }

    fun test(view: View){
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(view.context)
        recyclerView.addItemDecoration(
            DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL)
        )
        compositeDisposable.add(
            bdao.getAllBorrowers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        //データ取得完了時の処理
                        val data = ArrayList<Borrower>()
                        it.forEach {
                                user -> data.add(user)
                        }
                        val adapter = BorrowerListAdapter(data)
                        recyclerView.adapter = adapter
                    }
                )
        )
    }
}
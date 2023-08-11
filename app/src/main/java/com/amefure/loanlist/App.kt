package com.amefure.loanlist
import android.app.Application
import com.amefure.loanlist.Models.Room.RootRepository

class App : Application() {
    /**
     * [MemoRepository]のインスタンス
     */
    val rootRepository: RootRepository by lazy { RootRepository(this) }
}
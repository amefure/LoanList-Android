package com.amefure.loanlist.Models.DataStore

// MARK: - ソート設定

enum class SortItem {
    AMOUNT_ASCE {
        override fun message():String = "金額(昇順)"
    },
    AMOUNT_DESC {
        override fun message():String = "金額(降順)"
    },
    DATE_ASCE {
        override fun message():String = "日付(昇順)"
    },
    DATE_DESC {
        override fun message():String = "日付(降順)"
    },
    BORROW_ONLY {
        override fun message():String = "借のみ"
    },
    LOAN_ONLY {
        override fun message():String = "貸しのみ"
    },
    NONE {
        override fun message():String = "NONE"
    };
    abstract fun message(): String
}
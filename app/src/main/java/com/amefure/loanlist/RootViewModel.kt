package com.amefure.loanlist

import android.app.Application
import androidx.lifecycle.AndroidViewModel

abstract class RootViewModel(application: Application) : AndroidViewModel(application) {
    protected val rootRepository = (application as App).rootRepository
}

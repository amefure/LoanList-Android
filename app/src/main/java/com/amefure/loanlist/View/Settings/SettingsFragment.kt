package com.amefure.loanlist.View.Settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.amefure.loanlist.Models.DataStore.DataStoreManager
import com.amefure.loanlist.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SettingsFragment : Fragment() {

    lateinit var dataStoreManager: DataStoreManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataStoreManager = DataStoreManager(this.requireContext())
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val switch: Switch = view.findViewById(R.id.display_switch)

        observeAmountMark(view)

        switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                switch.setText("借")
                lifecycleScope.launch{
                    dataStoreManager.saveAmountMark("借")
                }
            } else {
                switch.setText("貸")
                lifecycleScope.launch{
                    dataStoreManager.saveAmountMark("貸")
                }
            }
        }
    }

    private fun observeAmountMark(view: View) {
        val switch: Switch = view.findViewById(R.id.display_switch)
        lifecycleScope.launch{
            dataStoreManager.observeAmountMark().collect {
                if (it == null || it == "借") {
                    switch.isChecked = true
                    switch.setText("借")
                } else {
                    switch.isChecked = false
                    switch.setText("貸")
                }
            }
        }
    }

}
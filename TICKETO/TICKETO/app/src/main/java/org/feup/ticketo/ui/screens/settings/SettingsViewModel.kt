package org.feup.ticketo.ui.screens.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class SettingsViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {
    val userInfo = mutableStateOf<Customer>(Customer(""))


    fun getUserInfoFromDatabase(){
        viewModelScope.launch {
            ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).let {
                userInfo.value = it
            }
        }
    }
}
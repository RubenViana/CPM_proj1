package org.feup.ticketo.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.feup.ticketo.data.storage.CreditCard
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.getUserIdInSharedPreferences

class SettingsViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {
    val userInfo = mutableStateOf<Customer?>(null)
    val cardInfo = mutableStateOf<CreditCard?>(null)

    fun getUserInfoFromDatabase(){
        viewModelScope.launch {
            ticketoStorage.getCustomer(getUserIdInSharedPreferences(context)).let {
                userInfo.value = it
            }
        }
        viewModelScope.launch {
            ticketoStorage.getCreditCardByCustomerId(getUserIdInSharedPreferences(context)).let {
                cardInfo.value = it
            }
        }
    }
}
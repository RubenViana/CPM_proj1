package org.feup.ticketo.ui.screens.register

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel

class RegisterViewModel(

) : ViewModel() {
    var username = mutableStateOf("")
    var nif = mutableStateOf("")
    var creditCardNumber = mutableStateOf("")
    var creditCardDate = mutableStateOf("")
    var creditCardType = mutableStateOf("")

    fun register() {

    }
}
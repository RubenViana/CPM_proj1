package org.feup.ticketo.ui.screens.register

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.launch
import org.feup.ticketo.data.serverMessages.CustomerRegistrationMessage
import org.feup.ticketo.data.serverMessages.ServerValidationState
import org.feup.ticketo.data.serverMessages.customerRegistrationMessage
import org.feup.ticketo.data.storage.CreditCard
import org.feup.ticketo.data.storage.Customer
import org.feup.ticketo.data.storage.TicketoStorage
import org.feup.ticketo.data.storage.storeUserIdInSharedPreferences
import org.feup.ticketo.utils.createAndStoreKeyPair
import org.feup.ticketo.utils.objectToJson
import org.feup.ticketo.utils.serverUrl
import org.json.JSONObject

class RegisterViewModel(
    private val context: Context,
    private val ticketoStorage: TicketoStorage
) : ViewModel() {
    var username = mutableStateOf("")
    var nif = mutableStateOf("")
    var creditCardNumber = mutableStateOf("")
    var creditCardDate = mutableStateOf("")
    var creditCardType = mutableStateOf("")
    private var publicKey: String? = null
    private lateinit var customer: Customer
    private lateinit var creditCard: CreditCard
    val serverValidationState = mutableStateOf<ServerValidationState?>(null)
    val showServerErrorToast = mutableStateOf(false)
    val showErrorToast = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    fun register() {

        publicKey = createAndStoreKeyPair()

        if (publicKey == null) {
            showErrorToast.value = true
            errorMessage.value = "Failure generating Key Pair"
            return
        }

        customer =
            Customer(
                customer_id = "",
                username = username.value,
                tax_number = nif.value.toInt(),
                public_key = publicKey
            )
        creditCard = CreditCard(
            credit_card_id = -1,
            type = creditCardType.value,
            number = creditCardNumber.value,
            validity = creditCardDate.value
        )

        val customerRegistrationMessage = customerRegistrationMessage(customer, creditCard)

        registerCustomerInServer(context, customerRegistrationMessage)

    }

    private fun registerCustomerInServer(
        context: Context,
        customerRegistrationMessage: CustomerRegistrationMessage
    ) {

        // Server endpoint
        val endpoint = "register_user"

        // Create the request body
        val json = objectToJson(customerRegistrationMessage)

        // Create the request
        val request = JsonObjectRequest(
            Request.Method.POST, serverUrl + endpoint, json,
            { response ->
                serverValidationState.value = ServerValidationState.Success(response)
                handleSuccessfulRegistration(response)
            },
            { error ->
                serverValidationState.value = ServerValidationState.Failure(error)
                showServerErrorToast.value = true
            }
        )

        // Add the request to the RequestQueue
        Volley.newRequestQueue(context).add(request)

    }

    private fun handleSuccessfulRegistration(response: JSONObject) {
        customer.customer_id = response.getString("customer_id")
        creditCard.credit_card_id = response.getInt("credit_card_id")
        viewModelScope.launch {
            try {
                ticketoStorage.insertCustomer(customer)
                ticketoStorage.insertCreditCard(creditCard)
            } catch (e: Exception) {
                showErrorToast.value = true
                errorMessage.value = "Failure storing user in database"
            }

        }

        storeUserIdInSharedPreferences(context, customer.customer_id)


    }


}
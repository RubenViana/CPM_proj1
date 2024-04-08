package org.feup.ticketvalidatorterminal.data

import com.android.volley.VolleyError

open class ServerValidationState {
    data object Success : ServerValidationState()
    data class Failure(val error: VolleyError) : ServerValidationState()
}
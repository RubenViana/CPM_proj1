package org.feup.ticketo.data.serverMessages

import com.android.volley.VolleyError
import org.json.JSONObject

open class ServerValidationState {
    data class Success(val response: JSONObject?, val message: String? = null) :
        ServerValidationState()

    data class Failure(val error: VolleyError?, val message: String? = null) :
        ServerValidationState()

    data class Loading(val message: String) : ServerValidationState()
}
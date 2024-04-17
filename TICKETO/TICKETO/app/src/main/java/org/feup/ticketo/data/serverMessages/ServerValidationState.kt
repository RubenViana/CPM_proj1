package org.feup.ticketo.data.serverMessages

import com.android.volley.VolleyError
import org.json.JSONObject

open class ServerValidationState {
    data class Success(val response: JSONObject?) : ServerValidationState()
    data class Failure(val error: VolleyError) : ServerValidationState()
    data class Loading(val message: String): ServerValidationState()
}
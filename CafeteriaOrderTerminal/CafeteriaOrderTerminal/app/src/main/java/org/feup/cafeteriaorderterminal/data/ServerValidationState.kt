package org.feup.cafeteriaorderterminal.data

import com.android.volley.VolleyError
import org.json.JSONObject

open class ServerValidationState {
    data class Success(val response: JSONObject) : ServerValidationState()
    data class Failure(val error: VolleyError) : ServerValidationState()
}
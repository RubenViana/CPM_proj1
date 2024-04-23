package org.feup.ticketvalidatorterminal.utils

import com.android.volley.VolleyError
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

fun getServerResponseErrorMessage (error: VolleyError): String? {
    return Json.decodeFromString<HashMap<String, String>>(error.networkResponse.data.decodeToString())["message"]
}
inline fun <reified T> byteArrayToObject(byteArray: ByteArray): T {
    return Json.decodeFromString(byteArrayToString(byteArray))
}

fun byteArrayToString(byteArray: ByteArray): String {
    return byteArray.toString(Charsets.UTF_8)
}

inline fun <reified T : Any> objectToByteArray(data: T) : ByteArray {
    return stringToByteArray(objectToJsonString(data))
}

inline fun <reified T : Any> objectToJsonString(data: T): String {
    return Json.encodeToString(data)
}

fun stringToByteArray(input: String): ByteArray {
    return input.toByteArray(Charsets.UTF_8)
}

inline fun <reified T : Any>  objectToJson (data: T): JSONObject {
    return JSONObject(Gson().toJson(data))
}

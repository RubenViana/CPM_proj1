package org.feup.ticketo.utils

import com.android.volley.VolleyError
import com.google.gson.Gson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject

<<<<<<<< HEAD:TICKETO/TICKETO/app/src/main/java/org/feup/ticketo/utils/Serialization.kt
fun getServerResponseErrorMessage(error: VolleyError?): String? {
    try {
        return Json.decodeFromString<HashMap<String, String>>(error!!.networkResponse.data.decodeToString())["message"]
    } catch (e: Exception) {
        return "Server is offline."
    }
========
fun getServerResponseErrorMessage (error: VolleyError): String? {
    return Json.decodeFromString<HashMap<String, String>>(error.networkResponse.data.decodeToString())["message"]
>>>>>>>> b8832931fa4f1e495e61984d0ada678b20daab41:TicketValidatorTerminal/app/src/main/java/org/feup/ticketvalidatorterminal/utils/Serialization.kt
}

inline fun <reified T> byteArrayToObject(byteArray: ByteArray): T {
    return Json.decodeFromString(byteArrayToString(byteArray))
}

fun byteArrayToString(byteArray: ByteArray): String {
    return byteArray.toString(Charsets.UTF_8)
}

inline fun <reified T : Any> objectToByteArray(data: T): ByteArray {
    return stringToByteArray(objectToJsonString(data))
}

inline fun <reified T : Any> objectToJsonString(data: T): String {
    return Json.encodeToString(data)
}

fun stringToByteArray(input: String): ByteArray {
    return input.toByteArray(Charsets.UTF_8)
}

inline fun <reified T : Any> objectToJson(data: T): JSONObject {
    return JSONObject(Gson().toJson(data))
}
package org.feup.ticketo.data.storage

import android.content.Context

// Store customer id in Shared Preferences
fun storeUserIdInSharedPreferences(context: Context, userId: String) {
    val sharedPreferences = context.getSharedPreferences("ticketo", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putString("customer_id", userId)
        apply()
    }
}

// Retrieve customer id from Shared Preferences
fun getUserIdInSharedPreferences(context : Context) : String {
    val sharedPreferences = context.getSharedPreferences("ticketo", Context.MODE_PRIVATE)
    return sharedPreferences.getString("customer_id", "").orEmpty()
}
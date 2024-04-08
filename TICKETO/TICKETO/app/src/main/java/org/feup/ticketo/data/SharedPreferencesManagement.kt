package org.feup.ticketo.data

import android.content.Context

fun storeUserIdInSharedPreferences(context: Context, userId: String) {
    val sharedPref = context.getSharedPreferences("ticketo", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("userId", userId)
        apply()
    }
}
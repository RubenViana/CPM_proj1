package org.feup.ticketo.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun formatDate(date: String): String {
    val originalDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(date)
    return SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(originalDate)
}
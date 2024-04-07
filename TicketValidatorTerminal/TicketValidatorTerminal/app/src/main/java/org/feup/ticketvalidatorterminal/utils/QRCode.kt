package org.feup.ticketvalidatorterminal.utils

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.ui.graphics.toArgb
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import org.feup.ticketvalidatorterminal.ui.theme.md_theme_light_onPrimary
import org.feup.ticketvalidatorterminal.ui.theme.md_theme_light_scrim
import java.util.Hashtable


// Example usage

//        val tvm = TicketValidationMessage(
//            "hello",
//            listOf(
//                mutableMapOf("ticket_id" to "ticket1"),
//                mutableMapOf("ticket_id" to "ticket2")
//            ),
//            "aojdgnsorj"
//        )
//
//
//        var qrCode: Bitmap? = generateQRCode(tvm)
//
//        when {
//            qrCode != null -> Image(
//            bitmap = qrCode.asImageBitmap(),
//            contentDescription = "QR Code"
//            )
//        }


inline fun <reified T : Any> generateQRCode(message: T): Bitmap? {

    val hints = Hashtable<EncodeHintType, String>().apply {
        put(
            EncodeHintType.CHARACTER_SET,
            "ISO-8859-1"
        )
    }
    val width = 600

    // convert message to byte array
    val byteArray = objectToByteArray(message)

    val result: BitMatrix
    try {
        result = MultiFormatWriter().encode(
            String(byteArray, Charsets.ISO_8859_1),
            BarcodeFormat.QR_CODE,
            width,
            width,
            hints
        )
    } catch (e: Exception) {
        Log.i("mytag_MultiFormatWriter_error", e.message.toString())
        return null
    }

    val pixels = IntArray(result.width * result.height)

    for (line in 0 until result.height) {
        val offset = line * result.width
        for (col in 0 until result.height) {
            pixels[offset + col] = if (result.get(
                    col,
                    line
                )
            ) md_theme_light_scrim.toArgb() else md_theme_light_onPrimary.toArgb()
        }
    }

    return Bitmap.createBitmap(
        result.width,
        result.height,
        Bitmap.Config.ARGB_8888
    ).apply {
        setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
    }

}
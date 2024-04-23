package org.feup.ticketo.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.lang.reflect.Field
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.util.Calendar
import java.util.GregorianCalendar

fun createAndStoreKeyPair(): String? {

    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_RSA,
        "AndroidKeyStore"
    )
    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
        "customerKeyPair",
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    ).setKeySize(512)
        .run {
            setDigests(KeyProperties.DIGEST_SHA256)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            setCertificateNotBefore(GregorianCalendar().time)
            setCertificateNotAfter(GregorianCalendar().apply { add(Calendar.YEAR, 10) }.time)
            build()
        }

    kpg.initialize(parameterSpec)

    kpg.generateKeyPair()

    val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    val entry: KeyStore.Entry = ks.getEntry("customerKeyPair", null)

    return if (entry is KeyStore.PrivateKeyEntry) {
        Base64.encodeToString(entry.certificate.publicKey.encoded, Base64.NO_WRAP)
    } else {
        Log.w("mytag_createAndStoreKeyPairWarning", "Not an instance of a PrivateKeyEntry")
        null
    }


}

inline fun <reified T : Any> signMessageWithPrivateKey(data: T): T {
    val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    val entry: KeyStore.Entry = ks.getEntry("customerKeyPair", null)
    if (entry !is KeyStore.PrivateKeyEntry) {
        Log.w("mytag_signMessageWarning", "Not an instance of a PrivateKeyEntry")
        return data
    }

    val signatureInstance = Signature.getInstance("SHA256WithRSA").apply {
        initSign(entry.privateKey)
        update(objectToByteArray(data))
    }

    val signature = Base64.encodeToString(signatureInstance.sign(), Base64.NO_WRAP)

    // Set the signature field dynamically using Kotlin reflection
    val signatureField: Field? = try {
        data::class.java.getDeclaredField("signature")
    } catch (e: NoSuchFieldException) {
        null
    }
    signatureField?.let {
        it.isAccessible = true
        it.set(data, signature)
    }

    return data
}
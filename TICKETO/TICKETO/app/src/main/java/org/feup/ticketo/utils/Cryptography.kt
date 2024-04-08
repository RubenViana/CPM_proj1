package org.feup.ticketo.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.lang.reflect.Field
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature

fun createAndStoreKeyPair(context: Context): KeyPair? {

    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_EC,
        "AndroidKeyStore"
    )
    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
        "customerKeyPair",
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    ).run {
        setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        build()
    }

    kpg.initialize(parameterSpec)

    return kpg.generateKeyPair()


}

inline fun <reified T : Any> signMessageWithPrivateKey(data: T) : T {
    val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    val entry: KeyStore.Entry = ks.getEntry("customerKeyPair", null)
    if (entry !is KeyStore.PrivateKeyEntry) {
        Log.w("mytag_signMessageWarning", "Not an instance of a PrivateKeyEntry")
        return data
    }

    val signatureAlgorithm = "SHA256withECDSA"
    val signatureInstance = Signature.getInstance(signatureAlgorithm).apply {
        initSign(entry.privateKey)
        update(objectToByteArray(data))
    }
    val signature = android.util.Base64.encodeToString(signatureInstance.sign(), android.util.Base64.DEFAULT)

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
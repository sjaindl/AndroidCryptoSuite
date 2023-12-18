package com.sjaindl.cryptosuite

import android.content.Context
import android.content.pm.PackageManager
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.security.Key
import java.security.KeyStore
import java.util.Date
import javax.crypto.SecretKey

class KeyStoreWrapper(
    private val context: Context,
) {
    fun storeKey(
        alias: String,
        key: SecretKey,
        storeInStrongBoxIfAvailable: Boolean = true,

        // Cryptographic key use authorization:
        purposes: Int = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        blockMode: String? = null,
        padding: String? = null,

        // Validity key use authorization:
        validFrom: Date? = null,
        validTo: Date? = null,

        // User authenticated key use authorization:
        authentication: UserAuthentication? = null,
        unlockedDeviceRequired: Boolean = false,
    ) {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null) // Additional password protection possible
        }

        val keyProtection = KeyProtection.Builder(
            purposes,
        ).apply {
            blockMode?.let {
                setBlockModes(it)
            }

            padding?.let {
                setEncryptionPaddings(it)
            }

            setKeyValidityStart(validFrom)
            setKeyValidityEnd(validTo)

            authentication?.let {
                setUserAuthenticationRequired(true)
                setUserAuthenticationParameters(it.timeout, it.types)
            }

            setInvalidatedByBiometricEnrollment(true)

            setUnlockedDeviceRequired(unlockedDeviceRequired)

            if (storeInStrongBoxIfAvailable && hasStrongBox()) {
                setIsStrongBoxBacked(true)
            }
        }.build()

        keystore.setEntry(
            alias,
            KeyStore.SecretKeyEntry(key),
            keyProtection,
        )
    }

    fun retrieveKey(alias: String): Key? {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        return keystore.getKey(alias, null)
    }

    fun containsKey(alias: String): Boolean {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        return keystore.containsAlias(alias)
    }

    fun deleteKey(alias: String) {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        keystore.deleteEntry(alias)
    }

    private fun hasStrongBox(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }
}

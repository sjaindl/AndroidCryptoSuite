package com.sjaindl.cryptosuite

import android.content.Context
import android.content.pm.PackageManager
import android.security.keystore.KeyProperties
import android.security.keystore.KeyProtection
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey

class KeyStoreWrapper(
    private val context: Context,
) {
    fun storeKey(
        alias: String,
        key: SecretKey,
        purposes: Int = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
    ) {
        storeKey(
            alias = alias,
            key = key,
            purposes = purposes,
            blockMode = null,
            padding = null,
            authentication = null,
            storeInStrongBoxIfAvailable = false,
            unlockedDeviceRequired = false,
        )
    }

    fun storeKey(
        alias: String,
        key: SecretKey,
        purposes: Int = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        blockMode: String? = KeyProperties.BLOCK_MODE_GCM,
        padding: String? = KeyProperties.ENCRYPTION_PADDING_NONE,
        authentication: UserAuthentication? = null,
        storeInStrongBoxIfAvailable: Boolean = true,
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

            authentication?.let {
                setUserAuthenticationRequired(true)
                setUserAuthenticationParameters(it.timeout, it.types)
            }

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

    private fun hasStrongBox(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }
}

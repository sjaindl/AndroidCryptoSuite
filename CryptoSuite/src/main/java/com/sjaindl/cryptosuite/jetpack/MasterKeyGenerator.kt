package com.sjaindl.cryptosuite.jetpack

import android.content.Context
import android.content.pm.PackageManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKeys
import com.sjaindl.cryptosuite.UserAuthentication

class MasterKeyGenerator(
    private val context: Context,
) {
    fun createDefault(): String {
        return MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    }

    fun createCustom(
        keystoreAlias: String = "master_key",
        purposes: Int = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        blockMode: String? = KeyProperties.BLOCK_MODE_GCM,
        padding: String? = KeyProperties.ENCRYPTION_PADDING_NONE,
        keySize: Int = 256,
        authentication: UserAuthentication? = null,
        storeInStrongBoxIfAvailable: Boolean = true,
        unlockedDeviceRequired: Boolean = false,
    ): String {
        val advancedSpec = KeyGenParameterSpec.Builder(
            keystoreAlias,
            purposes,
        ).apply {
            setBlockModes(blockMode)
            setEncryptionPaddings(padding)
            setKeySize(keySize)

            authentication?.let {
                setUserAuthenticationRequired(true)
                setUserAuthenticationParameters(it.timeout, it.types)
            }

            setUnlockedDeviceRequired(unlockedDeviceRequired)

            if (storeInStrongBoxIfAvailable && hasStrongBox()) {
                setIsStrongBoxBacked(true)
            }
        }.build()

        return MasterKeys.getOrCreate(advancedSpec)
    }

    private fun hasStrongBox(): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_STRONGBOX_KEYSTORE)
    }
}

package com.sjaindl.cryptosuite.jetpack

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences

class EncryptedSharedPreferencesWrapper(
    private val context: Context,
) {
    private val masterKeyGenerator: MasterKeyGenerator by lazy {
        MasterKeyGenerator(context = context)
    }

    fun create(preferencesName: String): SharedPreferences {
        val masterKeyAlias = masterKeyGenerator.createDefault()

        return EncryptedSharedPreferences.create(
            preferencesName,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, // key encryption
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM, // value encryption
        )
    }
}

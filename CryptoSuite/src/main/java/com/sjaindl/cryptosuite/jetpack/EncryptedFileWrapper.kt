package com.sjaindl.cryptosuite.jetpack

import android.content.Context
import androidx.security.crypto.EncryptedFile
import java.io.File

class EncryptedFileWrapper(
    private val context: Context,
) {
    private val masterKeyGenerator: MasterKeyGenerator by lazy {
        MasterKeyGenerator(context = context)
    }

    fun create(
        file: File,
        keySetPrefName: String? = null, // The SharedPreferences file to store the keySet.
        keySetAlias: String? = null, // alias in the SharedPreferences file to store the keySet
    ): EncryptedFile {
        val masterKeyAlias = masterKeyGenerator.createDefault()

        val builder = EncryptedFile.Builder(
            file,
            context,
            masterKeyAlias,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).apply {
            keySetAlias?.let {
                setKeysetAlias(it)
            }
            keySetPrefName?.let {
                setKeysetPrefName(it)
            }
        }

        return builder.build()
    }
}

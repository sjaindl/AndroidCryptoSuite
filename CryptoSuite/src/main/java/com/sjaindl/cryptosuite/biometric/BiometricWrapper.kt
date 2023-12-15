package com.sjaindl.cryptosuite.biometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
import androidx.biometric.BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.sjaindl.cryptosuite.KeyStoreWrapper
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricWrapper(
    private val activity: FragmentActivity,
) {
    companion object {
        private const val TAG = "BiometricWrapper"

        private const val KEY_ALIAS = "biometrics_key"
    }

    private val keyStoreWrapper by lazy {
        KeyStoreWrapper(activity)
    }

    private val biometricManager by lazy {
        BiometricManager.from(activity)
    }

    fun buildPromptInfo(
        title: String,
        subTitle: String,
        confirmationRequired: Boolean = true,
        authenticators: Int = BIOMETRIC_STRONG or DEVICE_CREDENTIAL,
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(title)
            setSubtitle(subTitle)

            if (authenticators.and(DEVICE_CREDENTIAL) == 0) {
                setNegativeButtonText("Cancel")
            }

            setConfirmationRequired(confirmationRequired)
            setAllowedAuthenticators(authenticators)
        }.build()
    }

    fun shouldEnrollBiometrics(authenticators: Int): Boolean {
        return biometricManager.canAuthenticate(authenticators) == BIOMETRIC_ERROR_NONE_ENROLLED
    }

    fun isAuthenticationAvailable(authenticators: Int): Boolean {
        return when (biometricManager.canAuthenticate(authenticators)) {
            BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "App can authenticate using biometrics.")
                true
            }
            BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e(TAG, "No biometric features available on this device.")
                false
            }
            BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e(TAG, "Biometric features are currently unavailable.")
                false
            }
            BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d(TAG, "Biometric credentials currently not enrolled")
                false
            }

            BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.e(TAG, "Biometric security update required")
                false
            }

            else -> {
                Log.e(TAG, "Invalid biometric authentication status")
                false
            }
        }
    }

    // TODO: get auth type

    fun login(
        promptInfo: BiometricPrompt.PromptInfo
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errorDesc: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errorDesc)
                    Toast.makeText(activity,
                        "Authentication error: $errorDesc", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    val plaintext = "Hello world"
                    //val encoded = Base64.encodeToString(plaintext.toByteArray(), Base64.DEFAULT)
                    val cipher = result.cryptoObject?.cipher?.doFinal(plaintext.toByteArray())

                    Toast.makeText(activity,
                        "Authentication succeeded with cipher: $cipher!", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(activity, "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )

        // java.lang.IllegalArgumentException: Crypto-based authentication is not supported for Class 2 (Weak) biometrics.
        // + mapping

        val crypto = if (promptInfo.allowedAuthenticators.and(BIOMETRIC_WEAK) != 0) null else getCryptoObject()
        if (crypto != null) {
            biometricPrompt.authenticate(
                promptInfo,
                crypto,
            )
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun getCryptoObject(): BiometricPrompt.CryptoObject {
        if (!keyStoreWrapper.containsKey(KEY_ALIAS)) {

            generateSecretKey(KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                // Invalidate the keys if the user has registered a new biometric
                // credential, such as a new fingerprint. Can call this method only
                // on Android 7.0 (API level 24) or higher. The variable
                // "invalidatedByBiometricEnrollment" is true by default.
                .setInvalidatedByBiometricEnrollment(true)
                .build())
        }

        val key = getSecretKey()
        val cipher = getCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key)

        return BiometricPrompt.CryptoObject(cipher)
    }

    private fun getCipher(): Cipher {
        val transformation = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        return Cipher.getInstance(transformation)
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }
}

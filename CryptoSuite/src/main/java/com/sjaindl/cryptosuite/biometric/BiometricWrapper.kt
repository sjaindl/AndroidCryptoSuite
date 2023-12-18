package com.sjaindl.cryptosuite.biometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
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
import javax.crypto.spec.IvParameterSpec

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

    private lateinit var iv: ByteArray

    fun buildPromptInfo(
        title: String,
        subTitle: String,
        confirmationRequired: Boolean = true,
        authenticators: Int = BIOMETRIC_STRONG or DEVICE_CREDENTIAL,
    ): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder().apply {
            setTitle(title)
            setSubtitle(subTitle)

            // Biometrics only require cancel button text
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

    fun login(
        promptInfo: BiometricPrompt.PromptInfo,
        isEncryptMode: Boolean,
        onSuccess: (Int, BiometricPrompt.CryptoObject?) -> Unit,
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        // Crypto-based authentication is not supported for Class 2 (Weak) biometrics:
        val cryptoSupported = promptInfo.allowedAuthenticators in listOf(BIOMETRIC_STRONG, DEVICE_CREDENTIAL, DEVICE_CREDENTIAL or BIOMETRIC_STRONG)

        val crypto = if (cryptoSupported) {
            getCryptoObject(isEncryptMode = isEncryptMode)
        } else {
            null
        }

        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errorDesc: CharSequence,
                ) {
                    super.onAuthenticationError(errorCode, errorDesc)
                    Log.e(TAG, "Authentication error: $errorDesc")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    // In case cipher couldn't be initialized before, init now after successful auth:
                    val cryptoObject = if (result.cryptoObject == null && cryptoSupported) {
                        getCryptoObject(isEncryptMode)
                    } else result.cryptoObject

                    onSuccess(result.authenticationType, cryptoObject)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e(TAG, "Authentication failed")
                }
            }
        )

        if (crypto != null) {
            biometricPrompt.authenticate(
                promptInfo,
                crypto,
            )
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun getCryptoObject(isEncryptMode: Boolean): BiometricPrompt.CryptoObject? {
        val key = getSecretKey()
        val cipher = getCipher()

        try {
            if (isEncryptMode) {
                cipher.init(Cipher.ENCRYPT_MODE, key)
                iv = cipher.iv
            } else {
                if (::iv.isInitialized) {
                    cipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
                } else {
                    return null
                }
            }
        } catch (exc: UserNotAuthenticatedException) {
            Log.e(TAG, "${exc.message}")
            return null
        }

        return BiometricPrompt.CryptoObject(cipher)
    }

    private fun getSecretKey(): SecretKey {
        if (!keyStoreWrapper.containsKey(KEY_ALIAS)) {
            return generateSecretKey(KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationParameters(10, KeyProperties.AUTH_DEVICE_CREDENTIAL or KeyProperties.AUTH_BIOMETRIC_STRONG)
                .setInvalidatedByBiometricEnrollment(true)
                .build())
        }

        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        keyStore.load(null)
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun getCipher(): Cipher {
        val transformation = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
        return Cipher.getInstance(transformation)
    }
}

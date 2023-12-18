package com.sjaindl.androidcryptosuite

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_BIOMETRIC
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt.AUTHENTICATION_RESULT_TYPE_UNKNOWN
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sjaindl.cryptosuite.biometric.BiometricWrapper

private const val tag = "BiometricsDemoScreen"

@Composable
fun BiometricsDemoScreen(
    biometricWrapper: BiometricWrapper,
) {
    var authenticators by remember {
        mutableIntStateOf(0)
    }

    var plaintext by remember {
        mutableStateOf("")
    }

    var cipher: ByteArray? by remember {
        mutableStateOf(null)
    }

    var authenticatedWith: String? by remember {
        mutableStateOf(null)
    }

    var encryptMode by remember {
        mutableStateOf(true)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_CANCELED) {
            login(
                isEncryptMode = encryptMode,
                plaintext = plaintext,
                cipher = cipher,
                authenticators = authenticators,
                biometricWrapper = biometricWrapper,
                onEnOrDecrypted = {
                    if (encryptMode) {
                        cipher = it
                    } else {
                        plaintext = String(it)
                    }
                },
                onAuthenticatedWith = {
                    authenticatedWith = it
                },
            )
        }
    }

    if (authenticators > 0) {
        loginOrEnroll(
            isEncryptMode = encryptMode,
            plaintext = plaintext,
            cipher = cipher,
            authenticators = authenticators,
            biometricWrapper = biometricWrapper,
            launcher = launcher,
            onEnOrDecrypted = {
                if (encryptMode) {
                    cipher = it
                } else {
                    plaintext = String(it)
                }
            },
            onAuthenticatedWith = {
                authenticatedWith = it
            },
        )

        authenticators = 0
        authenticatedWith = ""
    }

    Column(
        modifier = Modifier.padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Switch(
                checked = encryptMode,
                onCheckedChange = {
                    encryptMode = it
                    if (it) {
                        cipher = null
                    } else {
                        plaintext = ""
                    }
                },
            )

            Text(
                text = if (encryptMode) "Encrypt" else "Decrypt",
            )
        }


        OutlinedTextField(
            value = plaintext,
            onValueChange = {
                plaintext = it
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = encryptMode,
            supportingText = {
                Text(text = "Plaintext")
            }
        )

        OutlinedTextField(
            value = cipherText(cipher = cipher),
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = !encryptMode,
            supportingText = {
                Text(text = "Ciphertext")
            }
        )

        OutlinedTextField(
            value = authenticatedWith.orEmpty(),
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
            supportingText = {
                Text(text = "authenticated with")
            }
        )


        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK
            },
        ) {
            Text(text = "Weak biometrics")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
            },
        ) {
            Text(text = "Strong biometrics")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                authenticators = BiometricManager.Authenticators.DEVICE_CREDENTIAL
            },
        ) {
            Text(text = "Device credentials")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                authenticators = BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                        BiometricManager.Authenticators.BIOMETRIC_WEAK
            },
        ) {
            Text(text = "Device credentials or weak biometrics")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                authenticators = BiometricManager.Authenticators.DEVICE_CREDENTIAL or
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
            },
        ) {
            Text(text = "Device credentials or strong biometrics")
        }
    }
}

private fun cipherText(cipher: ByteArray?) = if (cipher != null) Base64.encodeToString(cipher, Base64.DEFAULT) else "Not supported"

private fun loginOrEnroll(
    isEncryptMode: Boolean,
    plaintext: String,
    cipher: ByteArray?,
    authenticators: Int,
    biometricWrapper: BiometricWrapper,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    onEnOrDecrypted: (ByteArray) -> Unit,
    onAuthenticatedWith: (String?) -> Unit,
) {
    if (biometricWrapper.shouldEnrollBiometrics(authenticators = authenticators)) {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                authenticators,
            )
        }

        launcher.launch(enrollIntent)
    } else {
        login(
            isEncryptMode = isEncryptMode,
            plaintext = plaintext,
            cipher = cipher,
            authenticators = authenticators,
            biometricWrapper = biometricWrapper,
            onEnOrDecrypted = onEnOrDecrypted,
            onAuthenticatedWith = onAuthenticatedWith,
        )
    }
}

private fun login(
    isEncryptMode: Boolean,
    plaintext: String,
    cipher: ByteArray?,
    authenticators: Int,
    biometricWrapper: BiometricWrapper,
    onEnOrDecrypted: (ByteArray) -> Unit,
    onAuthenticatedWith: (String?) -> Unit,
) {
    val authenticationAvailable = biometricWrapper.isAuthenticationAvailable(authenticators = authenticators)

    if (authenticationAvailable) {
        val promptInfo = biometricWrapper.buildPromptInfo(
            title = "Biometrics Demo",
            subTitle = "Authentication",
            confirmationRequired = true,
            authenticators = authenticators,
        )

        biometricWrapper.login(promptInfo = promptInfo, isEncryptMode = isEncryptMode) { authenticationType, cryptoObject ->
            val authenticatedWith = when(authenticationType) {
                AUTHENTICATION_RESULT_TYPE_UNKNOWN -> "Unknown"
                AUTHENTICATION_RESULT_TYPE_DEVICE_CREDENTIAL -> "Device credential"
                AUTHENTICATION_RESULT_TYPE_BIOMETRIC -> "Biometrics"
                else -> null
            }
            onAuthenticatedWith(authenticatedWith)

            if (isEncryptMode) {
                cryptoObject?.cipher?.doFinal(plaintext.toByteArray())?.let {
                    onEnOrDecrypted(it)
                }
            } else {
                cryptoObject?.cipher?.doFinal(cipher)?.let {
                    onEnOrDecrypted(it)
                }
            }
        }
    } else {
        Log.e(tag, "Authentication unavailable")
    }
}

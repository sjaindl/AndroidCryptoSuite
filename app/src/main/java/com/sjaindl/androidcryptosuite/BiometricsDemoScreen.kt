package com.sjaindl.androidcryptosuite

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.sjaindl.cryptosuite.biometric.BiometricWrapper

private const val tag = "BiometricsDemoScreen"

@Composable
fun BiometricsDemoScreen(
    biometricWrapper: BiometricWrapper,
) {
    var authenticators by remember {
        mutableIntStateOf(0)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != FragmentActivity.RESULT_CANCELED) {
            login(
                authenticators = authenticators,
                biometricWrapper = biometricWrapper,
            )
        }
    }

    if (authenticators > 0) {
        login(
            authenticators = authenticators,
            biometricWrapper = biometricWrapper,
            launcher = launcher,
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
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
                    authenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                },
            ) {
                Text(text = "Any biometrics")
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
                            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                },
            ) {
                Text(text = "Device credentials or biometrics")
            }
        }
    }
}

private fun login(
    authenticators: Int,
    biometricWrapper: BiometricWrapper,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    if (biometricWrapper.shouldEnrollBiometrics(authenticators = authenticators)) {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                authenticators,
            )
        }

        launcher.launch(enrollIntent)
    }

    login(
        authenticators = authenticators,
        biometricWrapper = biometricWrapper,
    )
}

private fun login(
    authenticators: Int,
    biometricWrapper: BiometricWrapper,
) {
    val authenticationAvailable = biometricWrapper.isAuthenticationAvailable(authenticators = authenticators)

    if (authenticationAvailable) {
        val promptInfo = biometricWrapper.buildPromptInfo(
            title = "Biometrics Demo",
            subTitle = "Authentication",
            confirmationRequired = true,
            authenticators = authenticators,
        )

        biometricWrapper.login(promptInfo = promptInfo)
    } else {
        Log.e(tag, "Authentication unavailable")
    }
}

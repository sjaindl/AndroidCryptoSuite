package com.sjaindl.androidcryptosuite

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.sjaindl.androidcryptosuite.ui.theme.AndroidCryptoSuiteTheme
import com.sjaindl.cryptosuite.biometric.BiometricWrapper

class MainActivity : FragmentActivity() {

    companion object {
        private const val tag = "MainActivity"
    }

    private val biometricWrapper by lazy {
        BiometricWrapper(activity = this)
    }
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var authenticators by remember {
                mutableIntStateOf(0)
            }

            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode != RESULT_CANCELED) {
                   login(authenticators = authenticators)
                }
            }

            if (authenticators > 0) {
                login(authenticators = authenticators, launcher = launcher)
            }

            AndroidCryptoSuiteTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column {
                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                authenticators = BIOMETRIC_WEAK
                            },
                        ) {
                            Text(text = "Biometric Weak")
                        }

                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                authenticators = BIOMETRIC_STRONG
                            },
                        ) {
                            Text(text = "Biometric Strong")
                        }

                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                authenticators = BIOMETRIC_WEAK or BIOMETRIC_STRONG
                            },
                        ) {
                            Text(text = "Any Biometric")
                        }

                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                authenticators = DEVICE_CREDENTIAL
                            },
                        ) {
                            Text(text = "Device credentials")
                        }

                        TextButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                authenticators = DEVICE_CREDENTIAL or BIOMETRIC_WEAK or BIOMETRIC_STRONG
                            },
                        ) {
                            Text(text = "Device credentials or biometrics")
                        }
                    }
                }
            }
        }
    }

    private fun login(
        authenticators: Int,
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

        login(authenticators = authenticators)
    }

    private fun login(authenticators: Int) {
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
}

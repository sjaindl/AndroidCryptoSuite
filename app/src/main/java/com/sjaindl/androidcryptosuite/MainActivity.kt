package com.sjaindl.androidcryptosuite

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.sjaindl.androidcryptosuite.ui.theme.AndroidCryptoSuiteTheme
import com.sjaindl.cryptosuite.biometric.BiometricWrapper

class MainActivity : FragmentActivity() {

    private val biometricWrapper by lazy {
        BiometricWrapper(activity = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidCryptoSuiteTheme {
                BiometricsDemoScreen(
                    biometricWrapper = biometricWrapper,
                )
            }
        }
    }
}

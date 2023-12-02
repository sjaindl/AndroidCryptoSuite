package com.sjaindl.cryptosuite

import android.security.keystore.KeyProperties

data class UserAuthentication(
    val timeout: Int, // duration in seconds or 0 if user authentication must take place for every use of the key.
    val types: Int = KeyProperties.AUTH_DEVICE_CREDENTIAL or KeyProperties.AUTH_BIOMETRIC_STRONG,
)

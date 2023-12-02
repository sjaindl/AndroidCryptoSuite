package com.sjaindl.cryptosuite.jetpack

import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EncryptedSharedPreferencesWrapperTests {
    @Test
    fun testPreferenceStorage() {
        val encryptedSharedPreferencesWrapper = EncryptedSharedPreferencesWrapper(
            context = InstrumentationRegistry.getInstrumentation().context,
        )

        val encryptedPreferences = encryptedSharedPreferencesWrapper.create("encrypted")

        encryptedPreferences.edit().apply {
            putString("key", "testValue")
        }.commit()

        val storedValue = encryptedPreferences.getString("key", null)

        assertThat(storedValue).isEqualTo("testValue")
    }
}

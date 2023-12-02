package com.sjaindl.cryptosuite.jetpack

import android.security.keystore.KeyProperties
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MasterKeyGeneratorTests {
    @Test
    fun testDefault() {
        val masterKeyGenerator = MasterKeyGenerator(context = InstrumentationRegistry.getInstrumentation().context)
        val keyAlias = masterKeyGenerator.createDefault()

        assertThat(keyAlias).isEqualTo("_androidx_security_master_key_")
    }

    @Test
    fun testCustom() {
        val customKeyAlias = "custom_master_key"
        val masterKeyGenerator = MasterKeyGenerator(context = InstrumentationRegistry.getInstrumentation().context)
        val keyAlias = masterKeyGenerator.createCustom(
            keystoreAlias = customKeyAlias,
            purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            blockMode = KeyProperties.BLOCK_MODE_GCM,
            padding = KeyProperties.ENCRYPTION_PADDING_NONE,
            keySize = 256,
        )

        assertThat(keyAlias).isEqualTo(customKeyAlias)
    }
}

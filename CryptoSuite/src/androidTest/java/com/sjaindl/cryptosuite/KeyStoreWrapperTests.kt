package com.sjaindl.cryptosuite

import android.security.keystore.KeyProperties
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.symmetric.AES
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.BlockMode
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.KeySize
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Padding
import org.junit.Before
import org.junit.Test
import java.security.KeyStore
import javax.crypto.SecretKey

class KeyStoreWrapperTests {
    private val testAlias = "secret_test_key"

    @Before
    fun setup() {
        val keystore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }

        keystore.deleteEntry(testAlias)
    }

    @Test
    fun testSecretKeyStorage() {
        val aes = AES(
            blockMode = BlockMode.CBC,
            keySize = KeySize.AES_256,
            padding = Padding.NO_PADDING,
        )

        val keyStoreWrapper = KeyStoreWrapper(context = InstrumentationRegistry.getInstrumentation().context)

        assertThat(keyStoreWrapper.retrieveKey(testAlias)).isNull()

        keyStoreWrapper.storeKey(
            alias = testAlias,
            key = aes.key,
            purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
            blockMode = KeyProperties.BLOCK_MODE_CBC,
            padding = KeyProperties.ENCRYPTION_PADDING_NONE,
        )

        val storedKey = keyStoreWrapper.retrieveKey(testAlias) as? SecretKey

        assertThat(storedKey).isNotNull()
        // Key is not extractable from KeyStore, but certain properties can be checked:
        assertThat(storedKey?.algorithm).isEqualTo(KeyProperties.KEY_ALGORITHM_AES)
    }
}

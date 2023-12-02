package com.sjaindl.cryptosuite.symmetric

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.BlockMode
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Padding
import org.junit.Test

class AESTests {
    @Test
    fun testInitialization() {
        val aesWithECBMode = AES(blockMode = BlockMode.ECB)

        assertThat(aesWithECBMode.key).isNotNull()
        assertThat(aesWithECBMode.initializationVector).isNull()

        val aesWithCBCMode = AES(blockMode = BlockMode.CBC)
        Log.d("****", "${aesWithCBCMode.key.encoded.size}")
        Log.d("****", "${aesWithCBCMode.initializationVector!!.size}")
        assertThat(aesWithCBCMode.key).isNotNull()
        assertThat(aesWithCBCMode.initializationVector).isNotNull()
    }

    @Test
    fun testAllBlockModesNoPadding() {
        val plainText = "12345678123456"

        BlockMode.values().forEach {
            val aes = AES(blockMode = it, padding = Padding.NO_PADDING, padChar = '0')
            val cipherText = aes.encrypt(plainText = plainText)
            val decryptedText = aes.decrypt(cipherText = cipherText)

            assertThat(decryptedText).isEqualTo("${plainText}00")
        }
    }

    @Test
    fun testAllBlockModesWithPadding() {
        val plainText = "1234"

        BlockMode.values().forEach {
            val aes = AES(blockMode = it, padding = Padding.PKCS5PADDING)
            val cipherText = aes.encrypt(plainText = plainText)
            val decryptedText = aes.decrypt(cipherText = cipherText)

            assertThat(decryptedText).isEqualTo(plainText)
        }
    }
}

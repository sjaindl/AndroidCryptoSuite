package com.sjaindl.cryptosuite.symmetric

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.BlockMode
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Padding
import org.junit.Test

class TripleDESTests {
    @Test
    fun testInitialization() {
        val desWithECBMode = TripleDES(blockMode = BlockMode.ECB)

        assertThat(desWithECBMode.key).isNotNull()
        assertThat(desWithECBMode.initializationVector).isNull()

        val desWithCBCMode = TripleDES(blockMode = BlockMode.CBC)

        assertThat(desWithCBCMode.key).isNotNull()
        assertThat(desWithCBCMode.initializationVector).isNotNull()
    }

    @Test
    fun testAllBlockModesNoPadding() {
        val plainText = "1234567"

        BlockMode.values().forEach {
            val des = TripleDES(blockMode = it, padding = Padding.NO_PADDING, padChar = '0')
            val cipherText = des.encrypt(plainText = plainText)
            val decryptedText = des.decrypt(cipherText = cipherText)

            assertThat(decryptedText).isEqualTo("${plainText}0")
        }
    }

    @Test
    fun testAllBlockModesWithPadding() {
        val plainText = "1234"

        BlockMode.values().forEach {
            val des = TripleDES(blockMode = it, padding = Padding.PKCS5PADDING)
            val cipherText = des.encrypt(plainText = plainText)
            val decryptedText = des.decrypt(cipherText = cipherText)

            assertThat(decryptedText).isEqualTo(plainText)
        }
    }
}

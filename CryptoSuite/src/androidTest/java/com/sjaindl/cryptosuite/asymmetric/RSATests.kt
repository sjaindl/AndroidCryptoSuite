package com.sjaindl.cryptosuite.asymmetric

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import org.junit.Test

class RSATests {
    @Test
    fun testInitialization() {
        val rsa = RSA()

        assertThat(rsa.keyPair.private).isNotNull()
        assertThat(rsa.keyPair.public).isNotNull()
    }

    @Test
    fun testAllKeySizes() {
        val plainText = "secret text"

        listOf(KeySize.RSA_1024, KeySize.RSA_2048, KeySize.RSA_3072).forEach {
            val rsa = RSA(keySize = it)
            val cipherText = rsa.encrypt(plainText = plainText)
            val decryptedText = rsa.decrypt(cipherText = cipherText)

            assertThat(decryptedText).isEqualTo(plainText)
        }
    }
}

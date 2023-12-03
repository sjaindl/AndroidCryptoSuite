package com.sjaindl.cryptosuite.asymmetric

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import org.junit.Test

class ECCSignatureTests {
    @Test
    fun testInitialization() {
        val eccSignature = ECCSignature()

        assertThat(eccSignature.keyPair.private).isNotNull()
        assertThat(eccSignature.keyPair.public).isNotNull()
    }

    @Test
    fun testAllKeySizes() {
        val textToSign = "some important text"

        listOf(KeySize.ECC_224, KeySize.ECC_256).forEach {
            val eccSignature = ECCSignature(keySize = it)

            val signed = eccSignature.sign(text = textToSign)
            assertThat(signed).isNotNull()

            val verified = eccSignature.verify(text = textToSign, signature = signed)
            assertThat(verified).isTrue()

            val notVerified = eccSignature.verify(text = "some other text", signature = signed)
            assertThat(notVerified).isFalse()
        }
    }
}

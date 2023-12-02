package com.sjaindl.cryptosuite.asymmetric

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import org.junit.Test

class ECCTests {
    @Test
    fun testInitialization() {
        val ecc = ECC()

        assertThat(ecc.keyPair.private).isNotNull()
        assertThat(ecc.keyPair.public).isNotNull()
    }

    @Test
    fun testAllKeySizes() {
        val textToSign = "some important text"

        listOf(KeySize.ECC_224, KeySize.ECC_256).forEach {
            val ecc = ECC(keySize = it)

            val signed = ecc.signData(text = textToSign)
            assertThat(signed).isNotNull()

            val verified = ecc.verifyData(text = textToSign, signature = signed!!)
            assertThat(verified).isTrue()

            val notVerified = ecc.verifyData(text = "some other text", signature = signed)
            assertThat(notVerified).isFalse()
        }
    }
}

package com.sjaindl.cryptosuite.asymmetric

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import org.junit.Test
import java.math.BigInteger
import java.util.Base64

class DiffieHellmanTests {
    @Test
    fun testInitialization() {
        val dh = DiffieHellman()

        assertThat(dh.keyPair.private).isNotNull()
        assertThat(dh.keyPair.public).isNotNull()
    }

    @Test
    fun testSharedSecret() {
        val alice = DiffieHellman()
        val bob = DiffieHellman()

        alice.setOtherPartyPublicKey(bob.keyPair.public)
        bob.setOtherPartyPublicKey(alice.keyPair.public)

        val keyAlice = alice.agreement()?.encoded
        val keyBob = bob.agreement()?.encoded

        val key1Encoded = Base64.getEncoder().encodeToString(keyAlice)
        val key2Encoded = Base64.getEncoder().encodeToString(keyBob)

        assertThat(key1Encoded).isEqualTo(key2Encoded)
    }
}

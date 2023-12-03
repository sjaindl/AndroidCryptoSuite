package com.sjaindl.cryptosuite.asymmetric

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DiffieHellmanKeyExchangeTests {
    @Test
    fun testInitialization() {
        val dh = DiffieHellmanKeyExchange()

        assertThat(dh.keyPair.private).isNotNull()
        assertThat(dh.keyPair.public).isNotNull()
    }

    @Test
    fun testSharedSecret() {
        val alice = DiffieHellmanKeyExchange()
        val bob = DiffieHellmanKeyExchange()

        alice.setOtherPartyPublicKey(bob.keyPair.public)
        bob.setOtherPartyPublicKey(alice.keyPair.public)

        val keyAlice = alice.agreement()?.encoded
        val keyBob = bob.agreement()?.encoded

        assertThat(keyAlice).isEqualTo(keyBob)
    }
}

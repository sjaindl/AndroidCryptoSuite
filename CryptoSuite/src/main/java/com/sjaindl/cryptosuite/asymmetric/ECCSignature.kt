package com.sjaindl.cryptosuite.asymmetric

import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature

class ECCSignature(
    private val keySize: KeySize = KeySize.ECC_256,
) {
    lateinit var keyPair: KeyPair

    init {
        initKeys()
    }

    fun sign(text: String): ByteArray {
        val signatureAlgorithm = Signature.getInstance(Algorithm.SHA256_EC_DSA.value)
        signatureAlgorithm.initSign(keyPair.private)
        signatureAlgorithm.update(text.toByteArray())

        return signatureAlgorithm.sign()
    }

    fun verify(text: String, signature: ByteArray): Boolean {
        val signatureAlgorithm = Signature.getInstance(Algorithm.SHA256_EC_DSA.value)
        signatureAlgorithm.initVerify(keyPair.public)
        signatureAlgorithm.update(text.toByteArray())

        return signatureAlgorithm.verify(signature)
    }

    private fun initKeys() {
        val generator = KeyPairGenerator.getInstance(Algorithm.EC.value)
        generator.initialize(keySize.value)
        keyPair = generator.generateKeyPair()
    }
}

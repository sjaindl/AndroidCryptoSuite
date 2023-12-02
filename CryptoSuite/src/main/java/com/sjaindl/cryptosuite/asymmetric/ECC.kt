package com.sjaindl.cryptosuite.asymmetric

import android.security.keystore.KeyProperties.KEY_ALGORITHM_EC
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Signature
import java.util.Base64


class ECC(
    private val keySize: KeySize = KeySize.ECC_256,
) {
    lateinit var keyPair: KeyPair

    init {
        initKeys()
    }

    fun signData(text: String): ByteArray? {
        val encodedText = Base64.getEncoder().encodeToString(text.toByteArray())
        val data = Base64.getDecoder().decode(encodedText)

        val signatureAlgorithm: Signature = Signature.getInstance(Algorithm.SHA256_EC_DSA.value)
        signatureAlgorithm.initSign(keyPair.private)
        signatureAlgorithm.update(data)

        return signatureAlgorithm.sign()
    }

    fun verifyData(text: String, signature: ByteArray): Boolean {
        val encodedText = Base64.getEncoder().encodeToString(text.toByteArray())

        val signatureAlgorithm: Signature = Signature.getInstance(Algorithm.SHA256_EC_DSA.value)
        signatureAlgorithm.initVerify(keyPair.public)
        signatureAlgorithm.update(Base64.getDecoder().decode(encodedText))

        return signatureAlgorithm.verify(signature)
    }

    private fun initKeys() {
        val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM_EC)
        generator.initialize(keySize.value)
        keyPair = generator.generateKeyPair()
    }
}

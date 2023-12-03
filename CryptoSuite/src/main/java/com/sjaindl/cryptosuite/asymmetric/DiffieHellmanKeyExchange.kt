package com.sjaindl.cryptosuite.asymmetric

import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.symmetric.CryptoConstants
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.security.spec.ECGenParameterSpec
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey

class DiffieHellmanKeyExchange(
    private val cipher: String = CryptoConstants.Algorithm.AES.name,
    private val curveName: String = "secp521r1", // 521-bit prime field Weierstrass curve.
) {
    lateinit var keyPair: KeyPair
    private var otherPartyPublicKey: PublicKey? = null

    init {
         initKeys()
    }

    fun setOtherPartyPublicKey(key: PublicKey) {
        otherPartyPublicKey = key
    }

    fun agreement(): SecretKey? {
        // Init
        val keyAgreement = KeyAgreement.getInstance(Algorithm.ECDH.value)
        keyAgreement.init(keyPair.private)

        // agreement
        keyAgreement.doPhase(otherPartyPublicKey, true)

        // secret
        return keyAgreement.generateSecret(cipher)
    }

    private fun initKeys() {
        val parameterSpec = ECGenParameterSpec(curveName)
        val generator = KeyPairGenerator.getInstance(Algorithm.EC.value)
        generator.initialize(parameterSpec)

        keyPair = generator.generateKeyPair()
    }
}

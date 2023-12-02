package com.sjaindl.cryptosuite.hashing

import com.sjaindl.cryptosuite.hashing.CryptoConstants.Algorithm
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class HMAC(
    private val algorithm: Algorithm,
) {

    fun authenticate(
        key: ByteArray,
        textToAuthenticate: String,
    ): ByteArray {
        val keySpec = SecretKeySpec(key, algorithm.value)
        val mac  = Mac.getInstance(algorithm.value)
        mac.init(keySpec)

        mac.update(textToAuthenticate.toByteArray())

        return mac.doFinal()
    }
}

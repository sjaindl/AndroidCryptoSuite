package com.sjaindl.cryptosuite.hashing

import com.sjaindl.cryptosuite.hashing.CryptoConstants.Algorithm
import java.security.MessageDigest

class Hashing(
    private val algorithm: Algorithm,
) {

    fun hash(plainText: String): ByteArray {
        val md = MessageDigest.getInstance(algorithm.value)
        return md.digest(plainText.toByteArray())
    }
}

package com.sjaindl.cryptosuite.hashing

import com.sjaindl.cryptosuite.hashing.CryptoConstants.Algorithm
import java.security.MessageDigest

class Hashing(
    private val algorithm: Algorithm,
) {

    fun hash(text: String): ByteArray {
        val messageDigest = MessageDigest.getInstance(algorithm.value)
        return messageDigest.digest(text.toByteArray())
    }
}

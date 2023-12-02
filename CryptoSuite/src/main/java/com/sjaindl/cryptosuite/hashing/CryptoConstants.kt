package com.sjaindl.cryptosuite.hashing

object CryptoConstants {
    enum class Algorithm(val value: String) {
        MD5(value = "MD5"),
        SHA1(value = "SHA-1"),
        SHA224(value = "SHA-224"),
        SHA256(value = "SHA-256"),
        SHA384(value = "SHA-384"),
        SHA512(value = "SHA-512"),

        HMAC_MD5(value = "HMac-MD5"),
        HMAC_SHA1(value = "HMac-SHA1"),
        HMAC_SHA224(value = "HMac-SHA224"),
        HMAC_SHA256(value = "HMac-SHA256"),
        HMAC_SHA384(value = "HMac-SHA384"),
        HMAC_SHA512(value = "HMac-SHA512"),
    }
}

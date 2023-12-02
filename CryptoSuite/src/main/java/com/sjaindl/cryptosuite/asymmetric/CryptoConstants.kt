package com.sjaindl.cryptosuite.asymmetric

object CryptoConstants {
    enum class Algorithm(val value: String) {
        SHA256_EC_DSA(value = "SHA256withECDSA"), // SHA-256 Elliptic Curves Digital Signature Algorithm
        ECDH(value = "ECDH"), // Elliptic Curves Diffie-Hellman
    }

    enum class KeySize(val value: Int) {
        RSA_1024(value = 1024),
        RSA_2048(value = 2048),
        RSA_3072(value = 2072),
        ECC_224(value = 224),
        ECC_256(value = 256),
    }
}

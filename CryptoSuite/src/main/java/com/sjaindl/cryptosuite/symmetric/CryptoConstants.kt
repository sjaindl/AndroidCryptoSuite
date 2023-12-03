package com.sjaindl.cryptosuite.symmetric

import android.security.keystore.KeyProperties

object CryptoConstants {
    enum class Algorithm(val value: String) {
        DES(value = "DES"), // Data Encryption Standard
        TRIPLE_DES(value = KeyProperties.KEY_ALGORITHM_3DES), // Data Encryption Standard
        AES(value = KeyProperties.KEY_ALGORITHM_AES),
    }

    enum class BlockMode(val value: String) {
        ECB(value = "ECB"), // Electronic Codebook
        CBC(value = "CBC"), // Cipher Block Chaining
        CFB(value = "CFB"), // Cipher Feedback Mode
        OFB(value = "OFB"), // Output Feedback Mode
        CTR(value = "CTR"), // Counter Mode
    }

    enum class Padding(val value: String) {
        // No padding applied, input already needs to be padded
        NO_PADDING(value = "NoPadding"),

        /*
        All padded bytes have the same value - the number of bytes padded:
        If numberOfBytes(text) mod 8 == 7, text += 0x01
        If numberOfBytes(text) mod 8 == 6, text += 0x0202
        If numberOfBytes(text) mod 8 == 5, text += 0x030303
        ...
        If numberOfBytes(text) mod 8 == 1, text += 0x07070707070707
         */
        PKCS5PADDING(value = "PKCS5PADDING")
    }

    enum class KeySize(val value: Int) {
        AES_128(value = 128),
        AES_192(value = 192),
        AES_256(value = 256),
    }
}

package com.sjaindl.cryptosuite.asymmetric

import android.util.Base64
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher

class RSA(
    private val keySize: KeySize = KeySize.RSA_1024,
) {
    lateinit var keyPair: KeyPair

    private lateinit var encryptionCipher: Cipher
    private lateinit var decryptionCipher: Cipher

    init {
        generateKeysAndCiphers()
    }

    fun encrypt(plainText: String): String {
        val ciphertext = encryptionCipher.doFinal(plainText.toByteArray())
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    fun decrypt(cipherText: String): String {
        val plaintext = decryptionCipher.doFinal(
            Base64.decode(cipherText.toByteArray(), Base64.DEFAULT)
        )
        return String(plaintext)
    }

    private fun generateKeysAndCiphers() {
        initKeys()
        initCiphers()
    }

    private fun initKeys() {
        val generator = KeyPairGenerator.getInstance(Algorithm.RSA.value)
        generator.initialize(keySize.value)
        keyPair = generator.generateKeyPair()
    }

    private fun initCiphers() {
        encryptionCipher = Cipher.getInstance(Algorithm.RSA.value)
        decryptionCipher = Cipher.getInstance(Algorithm.RSA.value)

        encryptionCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        decryptionCipher.init(Cipher.DECRYPT_MODE, keyPair.private)
    }
}

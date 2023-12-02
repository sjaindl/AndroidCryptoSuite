package com.sjaindl.cryptosuite.asymmetric

import android.security.keystore.KeyProperties.KEY_ALGORITHM_RSA
import com.sjaindl.cryptosuite.asymmetric.CryptoConstants.KeySize
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.Base64
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
        return Base64.getEncoder().encodeToString(ciphertext)
    }

    fun decrypt(cipherText: String): String {
        val plaintext = decryptionCipher.doFinal(Base64.getDecoder().decode(cipherText.toByteArray()))
        return String(plaintext)
    }

    private fun generateKeysAndCiphers() {
        initKeys()
        initCiphers()
    }

    private fun initKeys() {
        val generator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA)
        generator.initialize(keySize.value)
        keyPair = generator.generateKeyPair()
    }

    private fun initCiphers() {
        encryptionCipher = Cipher.getInstance(KEY_ALGORITHM_RSA)
        decryptionCipher = Cipher.getInstance(KEY_ALGORITHM_RSA)

        encryptionCipher.init(Cipher.ENCRYPT_MODE, keyPair.public)
        decryptionCipher.init(Cipher.DECRYPT_MODE, keyPair.private)
    }
}

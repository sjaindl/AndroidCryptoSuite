package com.sjaindl.cryptosuite.symmetric

import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.BlockMode
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Padding
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class TripleDES(
    private val blockMode: BlockMode = BlockMode.CBC,
    private val padding: Padding = Padding.NO_PADDING,
    private val padChar: Char = ' ',
) {
    lateinit var key: SecretKey
    var initializationVector: ByteArray? = null

    private lateinit var encryptionCipher: Cipher
    private lateinit var decryptionCipher: Cipher

    private val random by lazy {
        SecureRandom()
    }

    init {
        generateKeysAndCiphers()
    }

    fun encrypt(plainText: String): String {
        val blockSize = encryptionCipher.blockSize // 8 bytes
        val text = when(padding) {
            // NoPadding requires to provide plaintext, which is a multiple of 8 bytes
            Padding.NO_PADDING -> plainText.padEnd(length = plainText.length + blockSize - plainText.length % blockSize, padChar = padChar)
            else -> plainText
        }

        val ciphertext = encryptionCipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(ciphertext)
    }

    fun decrypt(cipherText: String): String {
        val plaintext = decryptionCipher.doFinal(Base64.getDecoder().decode(cipherText.toByteArray()))
        return String(plaintext)
    }

    private fun generateKeysAndCiphers() {
        initKey()
        initCiphers()
    }

    private fun initKey() {
        val keygen = KeyGenerator.getInstance(Algorithm.TRIPLE_DES.value)
        key = keygen.generateKey() // 3x8 = 24 bytes key
    }

    private fun initCiphers() {
        // complete transformation, e.g.: "DESede/ECB/PKCS5PADDING"
        val transformation = "${Algorithm.TRIPLE_DES.value}/${blockMode.value}/${padding.value}"
        encryptionCipher = Cipher.getInstance(transformation)
        decryptionCipher = Cipher.getInstance(transformation)

        when (blockMode) {
            BlockMode.ECB -> {
                encryptionCipher.init(Cipher.ENCRYPT_MODE, key)
                decryptionCipher.init(Cipher.DECRYPT_MODE, key)
            }
            else -> {
                initializationVector = ByteArray(encryptionCipher.blockSize)
                random.nextBytes(initializationVector)
                val initializationVectorSpec = IvParameterSpec(initializationVector)

                encryptionCipher.init(Cipher.ENCRYPT_MODE, key, initializationVectorSpec)
                decryptionCipher.init(Cipher.DECRYPT_MODE, key, initializationVectorSpec)
            }
        }
    }
}

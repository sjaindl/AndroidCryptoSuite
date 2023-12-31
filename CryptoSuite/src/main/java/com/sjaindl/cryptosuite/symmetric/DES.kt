package com.sjaindl.cryptosuite.symmetric

import android.util.Base64
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.BlockMode
import com.sjaindl.cryptosuite.symmetric.CryptoConstants.Padding
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class DES(
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
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    fun decrypt(cipherText: String): String {
        val plaintext = decryptionCipher.doFinal(Base64.decode(cipherText.toByteArray(), Base64.DEFAULT))
        return String(plaintext)
    }

    private fun generateKeysAndCiphers() {
        initKey()
        initCiphers()
    }

    private fun initKey() {
        val keygen = KeyGenerator.getInstance(Algorithm.DES.value)
        key = keygen.generateKey() // 64 bits
    }

    private fun initCiphers() {
        // complete transformation, e.g.: "DES/CBC/PKCS5PADDING"
        val transformation = "${Algorithm.DES.value}/${blockMode.value}/${padding.value}"
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

package com.sjaindl.cryptosuite.hashing

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.hashing.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.print
import org.junit.Test

class HashingTests {
    @Test
    fun testHashing() {
        val text = "Hello world!"
        val md5Hash = Hashing(Algorithm.MD5)
        val sha1Hash = Hashing(Algorithm.SHA1)
        val sha224Hash = Hashing(Algorithm.SHA224)
        val sha256Hash = Hashing(Algorithm.SHA256)
        val sha384Hash = Hashing(Algorithm.SHA384)
        val sha512Hash = Hashing(Algorithm.SHA512)

        val hashedMd5 = md5Hash.hash(text)
        val hashedSha1 = sha1Hash.hash(text)
        val hashedSha224 = sha224Hash.hash(text)
        val hashedSha256 = sha256Hash.hash(text)
        val hashedSha384 = sha384Hash.hash(text)
        val hashedSha512 = sha512Hash.hash(text)

        hashedMd5.print(algorithm = "MD5")
        hashedSha1.print(algorithm = "SHA-1")
        hashedSha224.print(algorithm = "SHA-224")
        hashedSha256.print(algorithm = "SHA-256")
        hashedSha384.print(algorithm = "SHA-384")
        hashedSha512.print(algorithm = "SHA-512")

        assertThat(hashedMd5.size * 8).isEqualTo(128) // 128 bits
        assertThat(hashedSha1.size * 8).isEqualTo(160) // 160 bits
        assertThat(hashedSha224.size * 8).isEqualTo(224) // 224 bits
        assertThat(hashedSha256.size * 8).isEqualTo(256) // 256 bits
        assertThat(hashedSha384.size * 8).isEqualTo(384) // 384 bits
        assertThat(hashedSha512.size * 8).isEqualTo(512) // 512 bits
    }
}

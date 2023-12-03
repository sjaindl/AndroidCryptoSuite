package com.sjaindl.cryptosuite.hashing

import com.google.common.truth.Truth.assertThat
import com.sjaindl.cryptosuite.hashing.CryptoConstants.Algorithm
import com.sjaindl.cryptosuite.print
import com.sjaindl.cryptosuite.symmetric.AES
import org.junit.Test

class HMACTests {
    @Test
    fun testHMAC() {
        val text = "Hello world!"

        val key = AES().key.encoded
        
        val md5HMAC = HMAC(Algorithm.HMAC_MD5)
        val sha1HMAC = HMAC(Algorithm.HMAC_SHA1)
        val sha224HMAC = HMAC(Algorithm.HMAC_SHA224)
        val sha256HMAC = HMAC(Algorithm.HMAC_SHA256)
        val sha384HMAC = HMAC(Algorithm.HMAC_SHA384)
        val sha512HMAC = HMAC(Algorithm.HMAC_SHA512)

        val hmacMd5 = md5HMAC.authenticate(key = key, textToAuthenticate = text)
        val hmacSha1 = sha1HMAC.authenticate(key = key, textToAuthenticate = text)
        val hmacSha224 = sha224HMAC.authenticate(key = key, textToAuthenticate = text)
        val hmacSha256 = sha256HMAC.authenticate(key = key, textToAuthenticate = text)
        val hmacSha384 = sha384HMAC.authenticate(key = key, textToAuthenticate = text)
        val hmacSha512 = sha512HMAC.authenticate(key = key, textToAuthenticate = text)

        hmacMd5.print(algorithm = Algorithm.HMAC_MD5.value)
        hmacSha1.print(algorithm = Algorithm.HMAC_SHA1.value)
        hmacSha224.print(algorithm = Algorithm.HMAC_SHA224.value)
        hmacSha256.print(algorithm = Algorithm.HMAC_SHA256.value)
        hmacSha384.print(algorithm = Algorithm.HMAC_SHA384.value)
        hmacSha512.print(algorithm = Algorithm.HMAC_SHA512.value)

        assertThat(hmacMd5.size * 8).isEqualTo(128) // 128 bits
        assertThat(hmacSha1.size * 8).isEqualTo(160) // 160 bits
        assertThat(hmacSha224.size * 8).isEqualTo(224) // 224 bits
        assertThat(hmacSha256.size * 8).isEqualTo(256) // 256 bits
        assertThat(hmacSha384.size * 8).isEqualTo(384) // 384 bits
        assertThat(hmacSha512.size * 8).isEqualTo(512) // 512 bits
    }
}

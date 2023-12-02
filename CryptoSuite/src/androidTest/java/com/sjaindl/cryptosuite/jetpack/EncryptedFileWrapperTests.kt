package com.sjaindl.cryptosuite.jetpack

import androidx.security.crypto.EncryptedFile
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.io.File

class EncryptedFileWrapperTests {
    @Test
    fun testPreferenceStorage() {
        val encryptedFileWrapper = EncryptedFileWrapper(
            context = InstrumentationRegistry.getInstrumentation().context,
        )

        val file = File("")
        val encryptedFile = encryptedFileWrapper.create(
            file = file,
            keySetAlias = "encrypted",
        )

        assertThat(encryptedFile).isInstanceOf(EncryptedFile::class.java)
    }
}

package com.sjaindl.androidcryptosuite

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay

class PasskeyInfoRepository(
    private val context: Context,
) {
    suspend fun getPasskeyInfo(coroutineScope: CoroutineScope): Deferred<String> {
        return coroutineScope.async {
            delay(500) // mimic some backend communication delay

            val json = context.assets.open("create_passkey.json").bufferedReader().use {
                it.readText()
            }

            return@async json
        }
    }
}

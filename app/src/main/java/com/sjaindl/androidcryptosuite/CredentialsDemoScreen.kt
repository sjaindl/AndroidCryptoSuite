package com.sjaindl.androidcryptosuite

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sjaindl.androidcryptosuite.ui.theme.AndroidCryptoSuiteTheme
import com.sjaindl.cryptosuite.credentials.CredentialManagerWrapper

private const val tag = "CredentialsDemoScreen"

private const val json = "{\n" +
        "  \"challenge\": \"T1xCsnxM2DNL2KdK5CLa6fMhD7OBqho6syzInk_n-Uo\",\n" +
        "  \"allowCredentials\": [],\n" +
        "  \"timeout\": 1800000,\n" +
        "  \"userVerification\": \"required\",\n" +
        "  \"rpId\": \"credential-manager-app-test.glitch.me\"\n" +
        "}"

@Composable
fun CredentialsDemoScreen(
    credentialManagerWrapper: CredentialManagerWrapper,
    activityContext: Context,
    credentialViewModel: CredentialViewModel = viewModel(
        factory = CredentialViewModel.CredentialViewModelFactory(context = activityContext)
    ),
) {
    val createPasskeyRequestJson by credentialViewModel.passkeyRequestJson.collectAsState()

    if (createPasskeyRequestJson.isNotEmpty()) {
        credentialManagerWrapper.createPasskey(
            requestJson = createPasskeyRequestJson,
            preferImmediatelyAvailableCredentials = false,
        )

        credentialViewModel.requestPasskeyJson()
    }

    Column {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                credentialManagerWrapper.signIn(requestJson = json)
            },
        ) {
            Text(text = "Sign in")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                credentialViewModel.requestPasskeyJson()
            },
        ) {
            Text(text = "Register passkey")
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                credentialManagerWrapper.registerPassword(username = "user", password = "pw")
            },
        ) {
            Text(text = "Register password")
        }
    }
}

@Preview
@Composable
fun CredentialsDemoScreenPreview() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val credentialManagerWrapper = CredentialManagerWrapper(context = context, coroutineScope = scope)

    AndroidCryptoSuiteTheme {
        CredentialsDemoScreen(
            credentialManagerWrapper = credentialManagerWrapper,
            activityContext = context,
        )
    }
}

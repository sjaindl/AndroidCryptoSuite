package com.sjaindl.cryptosuite.credentials

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.credentials.CreateCredentialResponse
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private const val TAG = "CredentialManagerWrapper"

class CredentialManagerWrapper(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
) {
    private val credentialManager = CredentialManager.create(context)

    fun registerPassword(username: String, password: String) {
        // Initialize a CreatePasswordRequest object.
        val createPasswordRequest =
            CreatePasswordRequest(id = username, password = password)

        // Create credential and handle result.
        coroutineScope.launch {
            try {
                val result =
                    credentialManager.createCredential(
                        // Use an activity based context to avoid undefined
                        // system UI launching behavior.
                        context,
                        createPasswordRequest
                    )
                handleRegisterPasswordResult(result)
            } catch (e: CreateCredentialException) {
                handleFailure(e)
            }
        }
    }

    private fun handleRegisterPasswordResult(result: CreateCredentialResponse) {
        // TODO("Not yet implemented")
    }

    fun createPasskey(requestJson: String, preferImmediatelyAvailableCredentials: Boolean) {
        val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
            // Contains the request in JSON format. Uses the standard WebAuthn
            // web JSON spec.
            requestJson = requestJson,
            // Defines whether you prefer to use only immediately available
            // credentials, not hybrid credentials, to fulfill this request.
            // This value is false by default.
            preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
        )

        // Execute CreateCredentialRequest asynchronously to register credentials
        // for a user account. Handle success and failure cases with the result and
        // exceptions, respectively.
        coroutineScope.launch {
            try {
                val result = credentialManager.createCredential(
                    // Use an activity-based context to avoid undefined system
                    // UI launching behavior
                    context = context,
                    request = createPublicKeyCredentialRequest,
                )
                handlePasskeyRegistrationResult(result)
            } catch (e : CreateCredentialException){
                handleFailure(e)
            }
        }
    }

    private fun handlePasskeyRegistrationResult(result: CreateCredentialResponse) {
        // TODO("Not yet implemented")
    }

    private fun handleFailure(e: CreateCredentialException) {
        Log.e(TAG, "${e.type}: ${e.message}")

        when (e) {
            is CreatePublicKeyCredentialDomException -> {
                // Handle the passkey DOM errors thrown according to the
                // WebAuthn spec.

                //handlePasskeyError(e.domError)
            }
            is CreateCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to register the credential.
            }
            is CreateCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
            }
            is CreateCredentialProviderConfigurationException -> {
                // Your app is missing the provider configuration dependency.
                // Most likely, you're missing the
                // "credentials-play-services-auth" module.
            }
            is CreateCredentialUnknownException -> {

            }
            is CreateCredentialCustomException -> {
                // You have encountered an error from a 3rd-party SDK. If you
                // make the API call with a request object that's a subclass of
                // CreateCustomCredentialRequest using a 3rd-party SDK, then you
                // should check for any custom exception type constants within
                // that SDK to match with e.type. Otherwise, drop or log the
                // exception.
            }
            else -> Log.w(TAG, "Unexpected exception type ${e::class.java.name}")
        }
    }

    fun signIn(requestJson: String) {
        val request = buildSignInRequest(requestJson = requestJson)

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = context,
                    request = request,
                )

                handleSignIn(result = result)
            } catch (exception : GetCredentialException) {
                handleFailure(exception = exception)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        result.credential
        when (val credential = result.credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                // Share responseJson i.e. a GetCredentialResponse on your server to
                // validate and authenticate

                Log.d(TAG, "Logged in with json: $responseJson")
            }
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                // Use id and password to send to your server to validate
                // and authenticate

                Log.d(TAG, "Logged in with user: $username, password: $password")
            }
            is CustomCredential -> {
                // If you are also using any external sign-in libraries, parse them
                // here with the utility functions provided.
                if (credential.type == ExampleCustomCredential.TYPE)  {
                    try {
                        val ExampleCustomCredential = ExampleCustomCredential.createFrom(credential.data)
                        // Extract the required credentials and complete the authentication as per
                        // the federated sign in or any external sign in library flow
                    } catch (e: ExampleCustomCredential.ExampleCustomCredentialParsingException) {
                        // Unlikely to happen. If it does, you likely need to update the dependency
                        // version of your external sign-in library.
                        Log.e(TAG, "Failed to parse an ExampleCustomCredential", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            } else -> {
            // Catch any unrecognized credential type here.
            Log.e(TAG, "Unexpected type of credential")
        }
        }
    }

    private fun handleFailure(exception: GetCredentialException) {
        Log.e(TAG, exception.localizedMessage ?: exception.message ?: "Could not sign in!")
    }

    private fun buildSignInRequest(requestJson: String): GetCredentialRequest {
        return GetCredentialRequest(
            signInOptions(requestJson = requestJson)
        )
    }

    private fun signInOptions(requestJson: String): List<CredentialOption> {
        // Retrieves the user's saved password for your app from their
        // password provider.

        val passwordOption = GetPasswordOption()

        // Get passkey from the user's public key credential provider.
        val passkeyOption = GetPublicKeyCredentialOption(
            requestJson = requestJson,
        )

        return listOf(passwordOption, passkeyOption)
    }


}

data class ExampleCustomCredential(
    val token: String,
) {
    class ExampleCustomCredentialParsingException: Exception("Parsing ExampleCustomCredential failed")

    companion object {
        fun createFrom(data: Bundle): Any {
            TODO("Not yet implemented")
        }

        const val TYPE: String = "ExampleCustomCredential"
    }

}

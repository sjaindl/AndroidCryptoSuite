package com.sjaindl.androidcryptosuite

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CredentialViewModel(private val passkeyInfoRepository: PasskeyInfoRepository): ViewModel() {

    private val _passkeyRequestJson = MutableStateFlow("")
    val passkeyRequestJson = _passkeyRequestJson.asStateFlow()

    fun requestPasskeyJson() = viewModelScope.launch {
        val info = passkeyInfoRepository.getPasskeyInfo(coroutineScope = this).await()
        _passkeyRequestJson.value = info
    }

    fun createdPasskey() {
        _passkeyRequestJson.value = ""
    }

    class CredentialViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CredentialViewModel::class.java)) {
                val repo = PasskeyInfoRepository(context)

                @Suppress("UNCHECKED_CAST")
                return CredentialViewModel(passkeyInfoRepository = repo) as T
            }
            throw IllegalArgumentException("UNKNOWN VIEW MODEL CLASS")
        }
    }
}

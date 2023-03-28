package com.mandalorian.chatapp.viewModel

import androidx.lifecycle.viewModelScope
import com.mandalorian.chatapp.service.AuthService
import com.mandalorian.chatapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val authRepo: AuthService) : BaseViewModel() {
    val loginFinish: MutableSharedFlow<Unit> = MutableSharedFlow()
    val signUpComplete: MutableSharedFlow<Unit> = MutableSharedFlow()
    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")

    fun login() {
        if (Utils.validate(email.value, password.value)) {
            viewModelScope.launch {
                safeApiCall {
                    authRepo.login(email.value, password.value)
                    loginFinish.emit(Unit)
                }
            }
        } else {
            viewModelScope.launch {
                error.emit("Failed to Login, Please try again")
            }
        }
    }

    fun navigateToSignUp() {
        viewModelScope.launch {
            signUpComplete.emit(Unit)
        }
    }
}
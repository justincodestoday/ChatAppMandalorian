package com.mandalorian.chatapp.ui.presentation.register.viewModel

import androidx.lifecycle.viewModelScope
import com.mandalorian.chatapp.data.model.User
import com.mandalorian.chatapp.service.AuthService
import com.mandalorian.chatapp.ui.presentation.base.viewModel.BaseViewModel
import com.mandalorian.chatapp.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val authRepo: AuthService) : BaseViewModel() {
    val signUpComplete: MutableSharedFlow<Unit> = MutableSharedFlow()
    val signInComplete: MutableSharedFlow<Unit> = MutableSharedFlow()
    val name: MutableStateFlow<String> = MutableStateFlow("")
    val email: MutableStateFlow<String> = MutableStateFlow("")
    val password: MutableStateFlow<String> = MutableStateFlow("")
    val confirmPassword: MutableStateFlow<String> = MutableStateFlow("")

    fun signUp() {
        if (Utils.validate(name.value, email.value, password.value)) {
            viewModelScope.launch {
                safeApiCall {
                    authRepo.register(
                        User(
                            username = name.value,
                            email = email.value,
                            password = password.value
                        )
                    )
                    signUpComplete.emit(Unit)
                }
            }
        } else {
            viewModelScope.launch {
                error.emit("Failed to Register, Please fill in all information")
            }
        }
    }

    fun navigateToSignIn() {
        viewModelScope.launch {
            signInComplete.emit(Unit)
        }
    }
}
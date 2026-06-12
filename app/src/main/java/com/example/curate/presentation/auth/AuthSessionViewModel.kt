package com.example.curate.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curate.domain.model.AuthState
import com.example.curate.domain.usecase.ObserveAuthStateUseCase
import com.example.curate.domain.usecase.RefreshAuthSessionUseCase
import com.example.curate.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthSessionViewModel @Inject constructor(
    observeAuthState: ObserveAuthStateUseCase,
    private val refreshAuthSession: RefreshAuthSessionUseCase,
    private val signOut: SignOutUseCase
) : ViewModel() {
    val authState: StateFlow<AuthState> = observeAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    init {
        viewModelScope.launch {
            runCatching {
                refreshAuthSession()
            }
        }
    }

    fun onSignOutClick() {
        viewModelScope.launch {
            runCatching {
                signOut()
            }
        }
    }
}

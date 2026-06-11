package com.example.curate.domain.usecase

import com.example.curate.domain.repository.NetworkMonitor
import javax.inject.Inject

class IsNetworkAvailableUseCase @Inject constructor(
    private val networkMonitor: NetworkMonitor
) {
    operator fun invoke(): Boolean = networkMonitor.isOnline()
}

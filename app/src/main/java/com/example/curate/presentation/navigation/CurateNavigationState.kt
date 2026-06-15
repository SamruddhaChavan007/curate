package com.example.curate.presentation.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList

class CurateNavigationState(
    selectedDestination: TopLevelDestination = TopLevelDestination.FEED,
    feedStack: List<CurateNavKey> = listOf(CurateNavKey.Home),
    discoverStack: List<CurateNavKey> = listOf(CurateNavKey.Discover),
    searchStack: List<CurateNavKey> = listOf(CurateNavKey.Search),
    libraryStack: List<CurateNavKey> = listOf(CurateNavKey.Library)
) {
    var selectedDestination by mutableStateOf(selectedDestination)
        private set

    private val stacks = mapOf(
        TopLevelDestination.FEED to feedStack.toMutableStateStack(CurateNavKey.Home),
        TopLevelDestination.DISCOVER to discoverStack.toMutableStateStack(CurateNavKey.Discover),
        TopLevelDestination.SEARCH to searchStack.toMutableStateStack(CurateNavKey.Search),
        TopLevelDestination.LIBRARY to libraryStack.toMutableStateStack(CurateNavKey.Library)
    )

    val currentBackStack: SnapshotStateList<CurateNavKey>
        get() = stackFor(selectedDestination)

    val currentKey: CurateNavKey
        get() = currentBackStack.last()

    val isAtTopLevelRoot: Boolean
        get() = currentBackStack.size == 1 && currentKey == selectedDestination.rootKey

    fun select(destination: TopLevelDestination) {
        if (destination == selectedDestination) {
            popToRoot()
        } else {
            selectedDestination = destination
        }
    }

    fun push(key: CurateNavKey) {
        val stack = currentBackStack
        if (stack.lastOrNull() != key) {
            stack.add(key)
        }
    }

    fun replaceTop(key: CurateNavKey) {
        val stack = currentBackStack
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        }
        push(key)
    }

    fun pop(): Boolean {
        val stack = currentBackStack
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            true
        } else {
            false
        }
    }

    fun popToRoot() {
        val stack = currentBackStack
        while (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        }
    }

    fun stackFor(destination: TopLevelDestination): SnapshotStateList<CurateNavKey> {
        return checkNotNull(stacks[destination])
    }

    companion object {
        val Saver: Saver<CurateNavigationState, Any> = listSaver(
            save = { state ->
                listOf(
                    state.selectedDestination.name,
                    state.stackFor(TopLevelDestination.FEED).map(CurateNavKey::toSavedString),
                    state.stackFor(TopLevelDestination.DISCOVER).map(CurateNavKey::toSavedString),
                    state.stackFor(TopLevelDestination.SEARCH).map(CurateNavKey::toSavedString),
                    state.stackFor(TopLevelDestination.LIBRARY).map(CurateNavKey::toSavedString)
                )
            },
            restore = { restored ->
                CurateNavigationState(
                    selectedDestination = TopLevelDestination.valueOf(restored[0] as String),
                    feedStack = restored[1].toRestoredStack(),
                    discoverStack = restored[2].toRestoredStack(),
                    searchStack = restored[3].toRestoredStack(),
                    libraryStack = restored[4].toRestoredStack()
                )
            }
        )
    }
}

private fun Any.toRestoredStack(): List<CurateNavKey> {
    return (this as? List<*>).orEmpty()
        .mapNotNull { it as? String }
        .map(String::toCurateNavKey)
}

private fun List<CurateNavKey>.toMutableStateStack(rootKey: CurateNavKey): SnapshotStateList<CurateNavKey> {
    val sanitizedStack = takeIf { it.firstOrNull() == rootKey } ?: listOf(rootKey)
    return mutableStateListOf<CurateNavKey>().apply {
        addAll(sanitizedStack.ifEmpty { listOf(rootKey) })
    }
}

private fun CurateNavKey.toSavedString(): String {
    return when (this) {
        CurateNavKey.Home -> "home"
        CurateNavKey.Discover -> "discover"
        CurateNavKey.Search -> "search"
        CurateNavKey.Library -> "library"
        is CurateNavKey.SignIn -> "sign-in:${returnToPrevious}"
        is CurateNavKey.SignUp -> "sign-up:${returnToPrevious}"
        CurateNavKey.Account -> "account"
        is CurateNavKey.WallpaperDetail -> "wallpaper:${wallpaperId.encodeSavedValue()}"
    }
}

private fun String.toCurateNavKey(): CurateNavKey {
    return when {
        this == "home" -> CurateNavKey.Home
        this == "discover" -> CurateNavKey.Discover
        this == "search" -> CurateNavKey.Search
        this == "library" -> CurateNavKey.Library
        this == "sign-in" -> CurateNavKey.SignIn()
        this == "sign-up" -> CurateNavKey.SignUp()
        startsWith("sign-in:") -> CurateNavKey.SignIn(returnToPrevious = removePrefix("sign-in:").toBoolean())
        startsWith("sign-up:") -> CurateNavKey.SignUp(returnToPrevious = removePrefix("sign-up:").toBoolean())
        this == "account" -> CurateNavKey.Account
        startsWith("wallpaper:") -> CurateNavKey.WallpaperDetail(removePrefix("wallpaper:").decodeSavedValue())
        else -> CurateNavKey.Home
    }
}

private fun String.encodeSavedValue(): String {
    return replace("%", "%25").replace(":", "%3A")
}

private fun String.decodeSavedValue(): String {
    return replace("%3A", ":").replace("%25", "%")
}

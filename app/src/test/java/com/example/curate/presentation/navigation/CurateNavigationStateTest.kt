package com.example.curate.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurateNavigationStateTest {
    @Test
    fun `switching tabs preserves each tab stack`() {
        val state = CurateNavigationState()

        state.push(CurateNavKey.WallpaperDetail("one"))
        state.select(TopLevelDestination.SEARCH)
        state.push(CurateNavKey.SignIn())
        state.select(TopLevelDestination.FEED)

        assertEquals(CurateNavKey.WallpaperDetail("one"), state.currentKey)

        state.select(TopLevelDestination.SEARCH)

        assertEquals(CurateNavKey.SignIn(), state.currentKey)
    }

    @Test
    fun `selecting current tab pops it to root`() {
        val state = CurateNavigationState()

        state.push(CurateNavKey.WallpaperDetail("one"))
        state.select(TopLevelDestination.FEED)

        assertEquals(CurateNavKey.Home, state.currentKey)
        assertTrue(state.isAtTopLevelRoot)
    }

    @Test
    fun `back pops only active stack`() {
        val state = CurateNavigationState()
        state.push(CurateNavKey.WallpaperDetail("one"))
        state.select(TopLevelDestination.SEARCH)
        state.push(CurateNavKey.SignIn())

        assertTrue(state.pop())

        assertEquals(CurateNavKey.Search, state.currentKey)

        state.select(TopLevelDestination.FEED)

        assertEquals(CurateNavKey.WallpaperDetail("one"), state.currentKey)
    }

    @Test
    fun `pop to root keeps active tab selected`() {
        val state = CurateNavigationState()
        state.select(TopLevelDestination.LIBRARY)
        state.push(CurateNavKey.SignIn())

        state.popToRoot()

        assertEquals(TopLevelDestination.LIBRARY, state.selectedDestination)
        assertEquals(CurateNavKey.Library, state.currentKey)
        assertTrue(state.isAtTopLevelRoot)
    }

    @Test
    fun `root back is not consumed`() {
        val state = CurateNavigationState()

        assertFalse(state.pop())
        assertEquals(CurateNavKey.Home, state.currentKey)
    }
}

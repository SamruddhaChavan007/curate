package com.example.curate.presentation.navigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurateNavigationChromeViewModelTest {
    @Test
    fun `scrolling down hides bottom bar`() {
        val viewModel = CurateNavigationChromeViewModel()

        viewModel.onContentScroll(deltaY = -1f)

        assertFalse(viewModel.uiState.value.isBottomBarVisible)
    }

    @Test
    fun `scrolling up shows bottom bar immediately`() {
        val viewModel = CurateNavigationChromeViewModel()
        viewModel.hideBottomBar()

        viewModel.onContentScroll(deltaY = 1f)

        assertTrue(viewModel.uiState.value.isBottomBarVisible)
    }

    @Test
    fun `stationary scroll keeps current bottom bar visibility`() {
        val viewModel = CurateNavigationChromeViewModel()
        viewModel.hideBottomBar()

        viewModel.onContentScroll(deltaY = 0f)

        assertFalse(viewModel.uiState.value.isBottomBarVisible)
    }
}

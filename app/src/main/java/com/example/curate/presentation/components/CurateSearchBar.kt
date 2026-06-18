package com.example.curate.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.curate.ui.theme.CurateTheme
import com.example.curate.ui.theme.curateColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurateSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFocusChange: (Boolean) -> Unit = {},
    onSearch: () -> Unit = {}
) {
    val curateColors = MaterialTheme.curateColors
    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(10.dp)
            .onFocusChanged { onFocusChange(it.isFocused) },
        shape = RoundedCornerShape(30.dp),
        placeholder = { Text("Search...", color = curateColors.onSubtle) },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearch()
                focusManager.clearFocus()
            },
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = curateColors.onSurface,
            unfocusedTextColor = curateColors.onSurface,
            cursorColor = curateColors.onSurface,
            focusedContainerColor = curateColors.chrome,
            unfocusedContainerColor = curateColors.chrome,
            disabledContainerColor = curateColors.chrome,
            focusedLeadingIconColor = curateColors.onSubtle,
            unfocusedLeadingIconColor = curateColors.onFaint,
            focusedTrailingIconColor = curateColors.onSubtle,
            unfocusedTrailingIconColor = curateColors.onFaint,
            focusedPlaceholderColor = curateColors.onSubtle,
            unfocusedPlaceholderColor = curateColors.onSubtle,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = { onQueryChange("") }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            } else {
                IconButton(
                    onClick = { },
                    enabled = false
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Microphone"
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewCurateSearchBar() {
    CurateTheme {
        Scaffold(
            topBar = {
                CurateSearchBar(
                    query = "",
                    onQueryChange = {}
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(12.dp)
            ) {
                // Main screen content goes here
            }
        }
    }
}

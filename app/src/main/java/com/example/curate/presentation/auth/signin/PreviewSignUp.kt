package com.example.curate.presentation.auth.signin

import android.content.res.Configuration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.curate.presentation.auth.signup.CurateSignUp
import com.example.curate.ui.theme.CurateTheme

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    name = "Light Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewCurateSignUp() {
    CurateTheme(
        dynamicColor = false
    ) {
        CurateSignUp(
            uiState = com.example.curate.presentation.auth.signup.SignUpUiState(),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityClick = {},
            onSubmit = {},
            onBackClick = {},
            onSignInClick = {}
        )
    }
}

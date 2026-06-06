package pe.edu.upc.vacapp.iam.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.R
import pe.edu.upc.vacapp.iam.presentation.view.components.AuthTextField
import pe.edu.upc.vacapp.iam.presentation.view.components.BrandHeader
import pe.edu.upc.vacapp.iam.presentation.view.components.LoginBackdrop
import pe.edu.upc.vacapp.iam.presentation.view.components.PrimaryButton
import pe.edu.upc.vacapp.iam.presentation.viewmodel.AuthViewModel
import pe.edu.upc.vacapp.ui.theme.Cream95
import pe.edu.upc.vacapp.ui.theme.Emerald30
import pe.edu.upc.vacapp.ui.theme.Emerald90
import pe.edu.upc.vacapp.ui.theme.Error40
import pe.edu.upc.vacapp.ui.theme.OnSurfaceVariantLight

/**
 * Premium Login screen (light-only, brand identity).
 *
 * Layout (top to bottom):
 *  1. [LoginBackdrop] covers the full screen (including behind the system bars),
 *     so there is no visible "layer" between the form and the bottom of the
 *     device. The system bars simply tint over the gradient.
 *  2. A lightweight [BrandHeader] wordmark anchors the top.
 *  3. A subtle form card contains the inputs and the CTA.
 *  4. A secondary "create account" link at the bottom.
 *
 * Business logic is untouched: the screen still consumes
 * [AuthViewModel.user], [AuthViewModel.isLoading], [AuthViewModel.loginSuccess]
 * and [AuthViewModel.errorMessage].
 */
@Composable
fun Login(
    viewmodel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    goToRegister: () -> Unit
) {
    val user by viewmodel.user.collectAsState()
    val isLoading by viewmodel.isLoading.collectAsState()
    val loginSuccess by viewmodel.loginSuccess.collectAsState()
    val errorMessage by viewmodel.errorMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginSuccess) {
        if (loginSuccess == true) {
            onLoginSuccess()
            viewmodel.resetLoginSuccess()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewmodel.resetErrorMessage()
        }
    }

    Scaffold(
        // We paint the entire screen ourselves with LoginBackdrop, so the
        // Scaffold's own background just has to be non-transparent to avoid
        // a dark window-background bar showing under the system bars.
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        // Take control of the insets so the backdrop can paint under the
        // system bars; we re-apply navigationBars / statusBars / ime padding
        // on the scrollable column below.
        contentWindowInsets = WindowInsets(0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data: SnackbarData -> StyledSnackbar(data) }
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            LoginBackdrop()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                BrandHeader()

                Spacer(Modifier.height(20.dp))

                FormCard(
                    email = user.email,
                    password = user.password,
                    onEmailChange = viewmodel::updateEmail,
                    onPasswordChange = viewmodel::updatePassword,
                    onSubmit = viewmodel::login,
                    isLoading = isLoading
                )

                Spacer(Modifier.height(16.dp))

                CreateAccountRow(
                    onClick = {
                        viewmodel.clearUser()
                        goToRegister()
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Sub-composables kept private to the Login screen.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun FormCard(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 480.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp),
                clip = false,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.06f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeaderBlock()

            AuthTextField(
                value = email,
                onValueChange = { if (!it.contains(' ')) onEmailChange(it) },
                label = "Email",
                leadingIcon = painterResource(id = R.drawable.envelope_simple),
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                leadingIcon = painterResource(id = R.drawable.lock_key),
                isPassword = true,
                imeAction = ImeAction.Done,
                onImeAction = { if (!isLoading) onSubmit() }
            )

            PrimaryButton(
                label = "Sign In",
                onClick = onSubmit,
                isLoading = isLoading,
                enabled = !isLoading
            )
        }
    }
}

@Composable
private fun HeaderBlock() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Text(
            text = "Sign in to keep your ranch running smoothly.",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariantLight
        )
    }
}

@Composable
private fun CreateAccountRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "New to VacApp?",
            style = MaterialTheme.typography.bodyMedium,
            color = OnSurfaceVariantLight,
            textAlign = TextAlign.Center
        )
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            Text(
                text = "Create account",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Emerald30
            )
        }
    }
}

@Composable
private fun StyledSnackbar(data: SnackbarData) {
    Surface(
        modifier = Modifier
            .padding(12.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Snackbar(
            snackbarData = data,
            containerColor = Emerald90,
            contentColor = Emerald30,
            actionColor = Error40,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

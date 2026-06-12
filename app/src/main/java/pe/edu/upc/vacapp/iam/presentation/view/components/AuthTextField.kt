package pe.edu.upc.vacapp.iam.presentation.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import pe.edu.upc.vacapp.R

/**
 * High-fidelity text field used across auth screens.
 *
 * Built on top of Material 3's [OutlinedTextField] so we keep its built-in
 * a11y and floating-label behaviour. We add:
 *  - a smoothly animated border / leading-icon color transition on focus
 *  - a smoothly revealed inline error (AnimatedVisibility) with icon
 *  - consistent shape / paddings from the design system
 *
 * When [isPassword] is true, an eye toggle is rendered and the input is masked
 * by default. [keyboardType] and [imeAction] flow straight into the field.
 * [errorText], when non-null, is shown beneath the field with a fade +
 * vertical-expand reveal.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    enabled: Boolean = true,
    errorText: String? = null,
    onImeAction: (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val errorColor = MaterialTheme.colorScheme.error
    val hasError = errorText != null

    val focusedTarget = if (hasError) errorColor else MaterialTheme.colorScheme.primary
    val unfocusedTarget =
        if (hasError) errorColor.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline

    val animatedBorderColor by animateColorAsState(
        targetValue = if (isFocused) focusedTarget else unfocusedTarget,
        animationSpec = tween(durationMillis = 240),
        label = "auth-field-border"
    )
    val animatedLeadingTint by animateColorAsState(
        targetValue = if (isFocused) focusedTarget
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(durationMillis = 240),
        label = "auth-field-leading-tint"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
                .semantics { contentDescription = label },
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize
            ),
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        painter = leadingIcon,
                        contentDescription = null,
                        tint = animatedLeadingTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = {
                if (isPassword) {
                    val visibilityIcon = if (passwordVisible) {
                        painterResource(id = R.drawable.eye_slash)
                    } else {
                        painterResource(id = R.drawable.eye)
                    }
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            painter = visibilityIcon,
                            contentDescription =
                                if (passwordVisible) "Hide password" else "Show password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = if (onImeAction != null) {
                KeyboardActions(
                    onDone = { onImeAction() },
                    onNext = { onImeAction() },
                    onGo = { onImeAction() },
                    onSearch = { onImeAction() },
                    onSend = { onImeAction() }
                )
            } else KeyboardActions.Default,
            label = { Text(text = label) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = animatedBorderColor,
                unfocusedBorderColor = unfocusedTarget,
                disabledBorderColor = unfocusedTarget.copy(alpha = 0.4f),
                focusedLeadingIconColor = animatedLeadingTint,
                unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedLabelColor = animatedBorderColor,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                cursorColor = focusedTarget,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    .copy(alpha = 0.5f)
            )
        )

        AnimatedVisibility(
            visible = hasError,
            enter = fadeIn(animationSpec = tween(200)) +
                expandVertically(animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(160)) +
                shrinkVertically(animationSpec = tween(180))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.x_circle),
                    contentDescription = null,
                    tint = errorColor,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = errorText.orEmpty(),
                    color = errorColor,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

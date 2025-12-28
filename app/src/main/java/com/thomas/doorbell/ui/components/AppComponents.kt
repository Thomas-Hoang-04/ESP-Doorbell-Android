package com.thomas.doorbell.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.thomas.doorbell.ui.theme.DarkPrimaryColor
import com.thomas.doorbell.ui.theme.DialogColor
import com.thomas.doorbell.ui.theme.LabelColor
import com.thomas.doorbell.ui.theme.PrimaryColor
import com.thomas.doorbell.ui.theme.TextColor
import com.thomas.doorbell.ui.theme.InputContainerColor

// ============================================================
// Input Types
// ============================================================

sealed class InputType {
    data object Text : InputType()
    data object Phone : InputType()
    data object Email : InputType()
    data object Password : InputType()
}

// ============================================================
// Annotated Text for Clickable Text Component
// ============================================================

sealed class AnnotatedText {
    data class Plain(val text: String) : AnnotatedText()
    data class Clickable(
        val text: String,
        val style: SpanStyle = SpanStyle(),
        val onClick: (String) -> Unit,
    ) : AnnotatedText()
}

// ============================================================
// Private Sub-Components
// ============================================================

@Composable
private fun LabelRow(label: String, required: Boolean) {
    Row {
        Text(
            text = label,
            color = LabelColor,
            style = TextStyles.label,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )
        if (required) {
            Spacer(modifier = Modifier.padding(Spacing.xs))
            Text(
                text = "*",
                color = Color.Red,
                style = TextStyles.label,
                modifier = Modifier.padding(bottom = Spacing.sm)
            )
        }
    }
}

@Composable
fun PasswordVisibilityToggle(
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.size(18.dp)
    ) {
        Icon(
            imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = if (isVisible) "Hide password" else "Show password",
            tint = Color.Gray
        )
    }
}

@Composable
private fun DialogButtonRow(
    canDismiss: Boolean,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    dismissText: String,
    acceptText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(Modifier.padding(horizontal = Spacing.lg).takeIf { !canDismiss } ?: Modifier),
        horizontalArrangement = Arrangement.Center
    ) {
        if (canDismiss) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = PrimaryColor
                )
            ) {
                Text(
                    text = dismissText,
                    style = TextStyles.dialogButton,
                    color = PrimaryColor
                )
            }
            Spacer(modifier = Modifier.width(Spacing.lg))
        }
        Button(
            onClick = onAccept,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(Spacing.md),
            modifier = Modifier.fillMaxWidth().takeIf { !canDismiss } ?: Modifier
        ) {
            Text(
                text = acceptText,
                style = TextStyles.dialogButton,
                color = Color.White
            )
        }
    }
}

// ============================================================
// Text Components
// ============================================================

@Composable
fun HeadingTextComponent(value: String, maxWidth: Boolean = true) {
    Text(
        text = value,
        modifier = Modifier
            .then(Modifier.fillMaxWidth().takeIf { maxWidth } ?: Modifier)
            .heightIn(),
        style = TextStyles.heading,
        color = TextColor,
        textAlign = TextAlign.Center
    )
}

@Composable
fun ClickableTextComponent(
    style: TextStyle = TextStyles.label.copy(fontWeight = FontWeight.Normal),
    vararg texts: AnnotatedText
) {
    val annotatedString = buildAnnotatedString {
        texts.forEach { text ->
            when (text) {
                is AnnotatedText.Plain -> append(text.text)
                is AnnotatedText.Clickable -> {
                    withLink(
                        LinkAnnotation.Clickable(tag = text.text) {
                            text.onClick(text.text)
                        }
                    ) {
                        withStyle(text.style) {
                            append(text.text)
                        }
                    }
                }
            }
        }
    }

    Text(
        text = annotatedString,
        style = style
    )
}

// ============================================================
// Input Components
// ============================================================

@Composable
fun TextInput(
    label: String,
    placeholder: String,
    state: TextFieldState,
    inputType: InputType = InputType.Text,
    required: Boolean = false,
    error: String? = null,
    onFocusChange: (Boolean) -> Unit = {},
    readOnly: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (error != null) Color.Red else Color.Gray,
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                color = InputContainerColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(10.dp)
            .then(if (readOnly) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onFocusChange(true) }
            else Modifier),
        verticalArrangement = Arrangement.Center
    ) {
        LabelRow(label = label, required = required)

        Spacer(Modifier.padding(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                if (inputType is InputType.Password) {
                    BasicSecureTextField(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChange(isFocused)
                            },
                        enabled = !readOnly,
                        textObfuscationMode = if (isPasswordVisible) 
                            TextObfuscationMode.Visible 
                        else 
                            TextObfuscationMode.RevealLastTyped,
                        textStyle = TextStyles.body.copy(color = Color.Black),
                        decorator = { innerText ->
                            Box(modifier = Modifier.background(Color.Transparent)) {
                                if (state.text.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = TextStyles.placeholder
                                    )
                                }
                                innerText()
                            }
                        }
                    )
                } else {
                    BasicTextField(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChange(isFocused)
                            },
                        enabled = !readOnly,
                        keyboardOptions = when (inputType) {
                            is InputType.Phone -> KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            )
                            is InputType.Email -> KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                            else -> KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        },
                        textStyle = TextStyles.body.copy(color = Color.Black),
                        decorator = { innerText ->
                            Box(modifier = Modifier.background(Color.Transparent)) {
                                if (state.text.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = TextStyles.placeholder
                                    )
                                }
                                innerText()
                            }
                        }
                    )
                }
            }

            if (inputType is InputType.Password) {
                PasswordVisibilityToggle(
                    isVisible = isPasswordVisible,
                    onToggle = { isPasswordVisible = !isPasswordVisible }
                )
            }
        }

        if (error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    modifier = Modifier.size(12.dp),
                    contentDescription = "Error",
                    tint = Color.Red,
                )
                Spacer(modifier = Modifier.padding(Spacing.xs))
                Text(
                    text = error,
                    style = TextStyles.error,
                    modifier = Modifier.padding(top = Spacing.sm)
                )
            }
        }
    }
}

@Composable
fun IconTextInput(
    icon: ImageVector,
    placeholder: String,
    state: TextFieldState,
    inputType: InputType = InputType.Text,
    error: String? = null,
    onFocusChange: (Boolean) -> Unit = {},
    readOnly: Boolean = false
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (error != null) Color.Red else Color.Gray,
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                color = InputContainerColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(10.dp)
            .then(if (readOnly) Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onFocusChange(true) }
            else Modifier),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = DarkPrimaryColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(Modifier.width(Spacing.md))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
            ) {
                if (inputType is InputType.Password) {
                    BasicSecureTextField(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChange(isFocused)
                            },
                        enabled = !readOnly,
                        textObfuscationMode = if (isPasswordVisible) 
                            TextObfuscationMode.Visible 
                        else 
                            TextObfuscationMode.RevealLastTyped,
                        textStyle = TextStyles.body.copy(color = Color.Black),
                        decorator = { innerText ->
                            Box(modifier = Modifier.background(Color.Transparent)) {
                                if (state.text.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = TextStyles.placeholder
                                    )
                                }
                                innerText()
                            }
                        }
                    )
                } else {
                    BasicTextField(
                        state = state,
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                                onFocusChange(isFocused)
                            },
                        enabled = !readOnly,
                        keyboardOptions = when (inputType) {
                            is InputType.Phone -> KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Next
                            )
                            is InputType.Email -> KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                            else -> KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        },
                        textStyle = TextStyles.body.copy(color = Color.Black),
                        decorator = { innerText ->
                            Box(modifier = Modifier.background(Color.Transparent)) {
                                if (state.text.isEmpty()) {
                                    Text(
                                        text = placeholder,
                                        style = TextStyles.placeholder
                                    )
                                }
                                innerText()
                            }
                        }
                    )
                }
            }

            if (inputType is InputType.Password) {
                PasswordVisibilityToggle(
                    isVisible = isPasswordVisible,
                    onToggle = { isPasswordVisible = !isPasswordVisible }
                )
            }
        }

        if (error != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    modifier = Modifier.size(12.dp),
                    contentDescription = "Error",
                    tint = Color.Red,
                )
                Spacer(modifier = Modifier.padding(Spacing.xs))
                Text(
                    text = error,
                    style = TextStyles.error,
                    modifier = Modifier.padding(top = Spacing.sm)
                )
            }
        }
    }
}

// ============================================================
// Button Components
// ============================================================

@Composable
fun CustomButton(
    value: String,
    color: ButtonColors,
    modifier: Modifier = Modifier,
    rounded: Dp = 10.dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .then(modifier),
        colors = color,
        enabled = enabled,
        shape = RoundedCornerShape(rounded)
    ) {
        Text(
            text = value,
            style = TextStyles.button
        )
    }
}

// ============================================================
// Dialog Components
// ============================================================

@Composable
fun CustomDialog(
    title: String,
    message: String = "",
    onDismiss: () -> Unit = { },
    onAccept: () -> Unit = { },
    canDismiss: Boolean = true,
    icon: ImageVector = Icons.Outlined.Info,
    dismissText: String = "Cancel",
    acceptText: String = "Confirm"
) {
    val windowSize = LocalWindowInfo.current.containerDpSize
    val width = windowSize.width
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .padding(Spacing.md)
                .height(if (!canDismiss) 180.dp else 200.dp)
                .width(if (!canDismiss) width * 2/3 else width * 0.72f)
                .background(DialogColor, shape = RoundedCornerShape(Spacing.md))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Info",
                    tint = PrimaryColor,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    style = TextStyles.dialogTitle,
                    color = TextColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(5.dp))

                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = message,
                        style = TextStyles.dialogBody,
                        color = TextColor,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                DialogButtonRow(
                    canDismiss = canDismiss,
                    onDismiss = onDismiss,
                    onAccept = onAccept,
                    dismissText = dismissText,
                    acceptText = acceptText
                )
            }
        }
    }
}

// ============================================================
// Loading Components
// ============================================================

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .background(Color.Gray.copy(alpha = 0.5f))
            .pointerInput(Unit) { },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PrimaryColor)
    }
}

// ============================================================
// Utility Functions
// ============================================================

fun makeToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
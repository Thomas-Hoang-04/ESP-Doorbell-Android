package com.thomas.doorbell.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.thomas.doorbell.R
import com.thomas.doorbell.nav.NavRoute
import com.thomas.doorbell.ui.components.AnnotatedText
import com.thomas.doorbell.ui.components.ClickableTextComponent
import com.thomas.doorbell.ui.components.CustomButton
import com.thomas.doorbell.ui.components.HeadingTextComponent
import com.thomas.doorbell.ui.components.IconTextInput
import com.thomas.doorbell.ui.components.InputType
import com.thomas.doorbell.ui.components.LoadingScreen
import com.thomas.doorbell.ui.components.Spacing
import com.thomas.doorbell.ui.components.TextStyles
import com.thomas.doorbell.ui.components.makeToast
import com.thomas.doorbell.ui.theme.LightPrimaryColor
import com.thomas.doorbell.ui.theme.PrimaryColor
import com.thomas.doorbell.viewmodel.AppViewModel
import com.thomas.doorbell.viewmodel.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginEntry(
    backStack: NavBackStack<NavKey>,
    appViewModel: AppViewModel,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val errorDescription = stringResource(R.string.error)
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value)
        LoadingScreen()

    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()
    val rememberMe by viewModel.rememberMe.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(R.drawable.login_bg),
                contentDescription = "Login Background",
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xxl, vertical = Spacing.xxxl),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeadingTextComponent("Welcome Back!")
                Spacer(modifier = Modifier.padding(Spacing.lg))
                IconTextInput(
                    icon = Icons.Filled.Person,
                    placeholder = stringResource(R.string.usr_prompt),
                    inputType = InputType.Text,
                    state = viewModel.username,
                    error = usernameError?.let { stringResource(it) },
                    onFocusChange = viewModel::onUsernameFocusChange,
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.md))
                IconTextInput(
                    icon = Icons.Filled.Password,
                    placeholder = stringResource(R.string.pwd_prompt),
                    inputType = InputType.Password,
                    state = viewModel.pwd,
                    error = passwordError?.let { stringResource(it) },
                    onFocusChange = viewModel::onPasswordFocusChange,
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = viewModel::rememberMe,
                            enabled = !isLoading.value,
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryColor,
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier
                                .size(Spacing.xxl)
                                .padding(start = Spacing.sm)
                                .scale(0.8f)
                        )
                        Text(
                            text = stringResource(R.string.acc_save),
                            style = TextStyles.label,
                            modifier = Modifier
                                .padding(start = Spacing.md)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = viewModel::rememberMe
                                )
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.forget_pwd),
                        style = TextStyles.label,
                        color = PrimaryColor,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { backStack.add(NavRoute.Auth.ForgetPassword) },
                    )
                }
                Spacer(modifier = Modifier.padding(Spacing.md))
                CustomButton(
                    value = stringResource(R.string.login),
                    color = ButtonColors(
                        contentColor = Color.White,
                        containerColor = PrimaryColor,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = LightPrimaryColor
                    )
                ) {
                    isLoading.value = true
                    viewModel.login(
                        onSuccess = { user ->
                            delay(150)
                            isLoading.value = false
                            appViewModel.updateUser(user)
                            onLoginSuccess()
                        },
                        onLoginFailed = { isLoading.value = false },
                        onFailed = {
                            isLoading.value = false
                            delay(150)
                            makeToast(context, errorDescription)
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                ClickableTextComponent(
                    texts = listOf(
                        AnnotatedText.Plain(stringResource(R.string.register_prompt_1)),
                        AnnotatedText.Clickable(
                            stringResource(R.string.register_prompt_2),
                            style = SpanStyle(color = PrimaryColor)
                        ) { backStack.add(NavRoute.Auth.Register) }
                    )
                )
            }
        }
    }
}

@Composable
@Preview
fun LoginEntryPreview() {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value)
        LoadingScreen()

    val username = TextFieldState("")
    val password = TextFieldState("")


    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                painter = painterResource(R.drawable.login_bg),
                contentDescription = "Login Background",
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.xxl, vertical = Spacing.xxxl),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HeadingTextComponent("Welcome Back!")
                Spacer(modifier = Modifier.padding(Spacing.lg))
                IconTextInput(
                    icon = Icons.Filled.Person,
                    placeholder = stringResource(R.string.usr_prompt),
                    inputType = InputType.Text,
                    state = username,
                    error = null,
                    onFocusChange = {  },
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.md))
                IconTextInput(
                    icon = Icons.Filled.Password,
                    placeholder = stringResource(R.string.pwd_prompt),
                    inputType = InputType.Password,
                    state = password,
                    error = "Wrong Password",
                    onFocusChange = {},
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.md))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = true,
                            onCheckedChange = {  },
                            enabled = !isLoading.value,
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryColor,
                                checkmarkColor = Color.White
                            ),
                            modifier = Modifier
                                .size(Spacing.xxl)
                                .padding(start = Spacing.sm)
                                .scale(0.8f)
                        )
                        Text(
                            text = stringResource(R.string.acc_save),
                            style = TextStyles.label,
                            modifier = Modifier
                                .padding(start = Spacing.md)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { }
                                )
                        )
                    }
                    Text(
                        text = stringResource(id = R.string.forget_pwd),
                        style = TextStyles.label,
                        color = PrimaryColor,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {

                            },
                    )
                }

                Spacer(modifier = Modifier.padding(Spacing.md))

                CustomButton(
                    value = stringResource(R.string.login),
                    color = ButtonColors(
                        contentColor = Color.White,
                        containerColor = PrimaryColor,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = LightPrimaryColor
                    )
                ) { }

                Spacer(modifier = Modifier.weight(1f))

                ClickableTextComponent(
                    texts = listOf(
                        AnnotatedText.Plain(stringResource(R.string.register_prompt_1)),
                        AnnotatedText.Clickable(
                            stringResource(R.string.register_prompt_2),
                            style = SpanStyle(color = PrimaryColor)
                        ) { }
                    )
                )
            }
        }
    }
}

@Composable
fun RegisterEntry() {

}

@Composable
@Preview
fun RegisterEntryPreview() {
}

@Composable
fun ForgetPasswordEntry() {}
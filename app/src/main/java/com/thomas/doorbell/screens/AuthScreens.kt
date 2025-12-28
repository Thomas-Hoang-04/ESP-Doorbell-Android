package com.thomas.doorbell.screens

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.thomas.doorbell.R
import com.thomas.doorbell.dto.OTPPurpose
import com.thomas.doorbell.dto.OTPRequest
import com.thomas.doorbell.nav.NavRoute
import com.thomas.doorbell.ui.components.AnnotatedText
import com.thomas.doorbell.ui.components.ClickableTextComponent
import com.thomas.doorbell.ui.components.CustomButton
import com.thomas.doorbell.ui.components.HeadingTextComponent
import com.thomas.doorbell.ui.components.IconTextInput
import com.thomas.doorbell.ui.components.InputType
import com.thomas.doorbell.ui.components.LoadingScreen
import com.thomas.doorbell.ui.components.Spacing
import com.thomas.doorbell.ui.components.TextInput
import com.thomas.doorbell.ui.components.TextStyles
import com.thomas.doorbell.ui.components.makeToast
import com.thomas.doorbell.ui.theme.BgPrimaryColor
import com.thomas.doorbell.ui.theme.ButtonColor
import com.thomas.doorbell.ui.theme.LightPrimaryColor
import com.thomas.doorbell.ui.theme.PrimaryColor
import com.thomas.doorbell.viewmodel.AppViewModel
import com.thomas.doorbell.viewmodel.LoginViewModel
import com.thomas.doorbell.viewmodel.RegisterViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@Composable
fun LoginEntry(
    backStack: NavBackStack<NavKey>,
    appViewModel: AppViewModel,
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val errorDescription = stringResource(R.string.error)
    val noInternetDescription = stringResource(R.string.net_error)
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value)
        LoadingScreen()

    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val passwordError by viewModel.passwordError.collectAsStateWithLifecycle()

    val isOnline by appViewModel.isOnline.collectAsStateWithLifecycle()

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
                Spacer(modifier = Modifier.padding(Spacing.lg))
                IconTextInput(
                    icon = Icons.Filled.Password,
                    placeholder = stringResource(R.string.pwd_prompt),
                    inputType = InputType.Password,
                    state = viewModel.pwd,
                    error = passwordError?.let { stringResource(it) },
                    onFocusChange = viewModel::onPasswordFocusChange,
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.lg))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                Spacer(modifier = Modifier.padding(Spacing.lg))
                CustomButton(
                    value = stringResource(R.string.login),
                    color = ButtonColors(
                        contentColor = Color.White,
                        containerColor = PrimaryColor,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = LightPrimaryColor
                    ),
                    rounded = 24.dp,
                    enabled = !isLoading.value
                ) {
                    if (isOnline) {
                        isLoading.value = true
                        viewModel.login(
                            onSuccess = { user ->
                                delay(150)
                                isLoading.value = false
                                appViewModel.updateUser(user)
                                if (user.isEmailVerified) onLoginSuccess()
                                else backStack.add(NavRoute.Auth.OTP(user.username, user.email))
                            },
                            onLoginFailed = { isLoading.value = false },
                            onFailed = {
                                isLoading.value = false
                                delay(150)
                                makeToast(context, errorDescription)
                            }
                        )
                    } else makeToast(context, noInternetDescription)
                }

                Spacer(modifier = Modifier.weight(1f))

                val registerPrompt1 = stringResource(R.string.register_prompt_1)
                val registerPrompt2 = stringResource(R.string.register_prompt_2)

                ClickableTextComponent(
                    style = TextStyles.body,
                    AnnotatedText.Plain("$registerPrompt1 "),
                    AnnotatedText.Clickable(
                        registerPrompt2,
                        style = SpanStyle(color = PrimaryColor)
                    ) { backStack.add(NavRoute.Auth.Register) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterEntry(
    backStack: NavBackStack<NavKey>,
    appViewModel: AppViewModel,
    viewModel: RegisterViewModel = hiltViewModel(),
    onLogin: () -> Unit
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value)
        LoadingScreen()

    val pwdVerifyLabel = stringResource(R.string.verify_pwd)
    val pwdVerifyPrompt = stringResource(R.string.pwd_verify_prompt)

    val usernameError by viewModel.usernameError.collectAsStateWithLifecycle()
    val emailError by viewModel.emailError.collectAsStateWithLifecycle()
    val pwdError by viewModel.passwordError.collectAsStateWithLifecycle()
    val confirmError by viewModel.confirmPwdError.collectAsStateWithLifecycle()
    val verified by appViewModel.isVerified.collectAsStateWithLifecycle()
    val loginFailed by appViewModel.verifiedButLoginFailed.collectAsStateWithLifecycle()
    
    var agreeToService by remember { mutableStateOf(false) }

    val noInternetDescription = stringResource(R.string.net_error)
    val errorDescription = stringResource(R.string.error)
    val isOnline by appViewModel.isOnline.collectAsStateWithLifecycle()

    if (verified && !loginFailed) {
        if (isOnline) {
            isLoading.value = true
            viewModel.firstLogin(
                onSuccess = { user ->
                    appViewModel.updateUser(user)
                    onLogin()
                },
                onFailed = { makeToast(context, errorDescription) }
            )
        } else {
            appViewModel.failedLogin()
            makeToast(context, noInternetDescription)
        }
    } else {
        Scaffold(
            containerColor = Color.White,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { HeadingTextComponent(stringResource(R.string.register), false) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = Spacing.xxl),
            ) {
                val loginPrompt1 = stringResource(R.string.login_prompt_1)
                val loginPrompt2 = stringResource(R.string.login_prompt_2)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClickableTextComponent(
                        style = TextStyles.body,
                        AnnotatedText.Plain("$loginPrompt1 "),
                        AnnotatedText.Clickable(
                            loginPrompt2,
                            style = SpanStyle(color = PrimaryColor)
                        ) { backStack.removeAt(backStack.lastIndex) }
                    )
                }

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
                Spacer(modifier = Modifier.padding(Spacing.lg))
                IconTextInput(
                    icon = Icons.Filled.Email,
                    placeholder = stringResource(R.string.email_prompt),
                    inputType = InputType.Email,
                    state = viewModel.email,
                    error = emailError?.let { stringResource(it) },
                    onFocusChange = viewModel::onEmailFocusChange,
                    readOnly = isLoading.value,
                )
                Spacer(modifier = Modifier.padding(Spacing.lg))
                IconTextInput(
                    icon = Icons.Filled.Password,
                    placeholder = stringResource(R.string.pwd_prompt),
                    inputType = InputType.Password,
                    state = viewModel.pwd,
                    error = pwdError?.let { stringResource(it) },
                    onFocusChange = viewModel::onPasswordFocusChange,
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.padding(Spacing.lg))
                TextInput(
                    label = pwdVerifyLabel,
                    placeholder = pwdVerifyPrompt,
                    state = viewModel.confirmPwd,
                    inputType = InputType.Password,
                    error = confirmError?.let { stringResource(it) },
                    onFocusChange = viewModel::onConfirmPwdFocusChange,
                    readOnly = isLoading.value
                )
                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.padding(end = Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val termsOfUse = stringResource(R.string.term_of_use)
                    val policy = stringResource(R.string.policy)
                    val agree = stringResource(R.string.agree)
                    val and = stringResource(R.string.and)

                    Checkbox(
                        checked = agreeToService,
                        onCheckedChange = { agreeToService = it },
                        enabled = !isLoading.value,
                        colors = CheckboxDefaults.colors(
                            checkedColor = PrimaryColor,
                            uncheckedColor = Color.Gray
                        )
                    )

                    ClickableTextComponent(
                        style = TextStyles.body,
                        AnnotatedText.Plain("$agree "),
                        AnnotatedText.Clickable(
                            termsOfUse,
                            style = SpanStyle(color = PrimaryColor)
                        ) {},
                        AnnotatedText.Plain(" $and "),
                        AnnotatedText.Clickable(
                            policy,
                            style = SpanStyle(color = PrimaryColor)
                        ) {}
                    )
                }

                Spacer(modifier = Modifier.padding(Spacing.md))

                CustomButton(
                    value = stringResource(R.string.register_btn),
                    color = ButtonColors(
                        contentColor = Color.White,
                        containerColor = PrimaryColor,
                        disabledContentColor = Color.Gray,
                        disabledContainerColor = LightPrimaryColor
                    ),
                    rounded = 24.dp,
                    enabled = !isLoading.value && agreeToService
                ) {
                    if (isOnline) {
                        isLoading.value = true
                        if (!verified) {
                            viewModel.registerAndVerify(
                                context,
                                onSuccess = { username, email ->
                                    delay(150)
                                    isLoading.value = false
                                    backStack.add(NavRoute.Auth.OTP(username, email, true))
                                },
                                onFailed = { msg ->
                                    isLoading.value = false
                                    makeToast(context, msg)
                                }
                            )
                        } else {
                            viewModel.firstLogin(
                                onSuccess = { user ->
                                    appViewModel.updateUser(user)
                                    onLogin()
                                },
                                onFailed = { makeToast(context, errorDescription) }
                            )
                        }
                    } else makeToast(context, noInternetDescription)
                }

                Spacer(modifier = Modifier.padding(Spacing.xl))

            }
        }
    }
}

@Composable
fun VerifyEmail(
    backStack: NavBackStack<NavKey>,
    appViewModel: AppViewModel,
    otpMetadata: NavRoute.Auth.OTP,
    onSuccessNoOrigin: () -> Unit
) {
    var otpValues by remember { mutableStateOf(List(6) { "" }) }
    val focusRequesters = remember { List(6) { FocusRequester() } }

    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val activity = LocalActivity.current as? ComponentActivity
    val focusManager = LocalFocusManager.current
    val isOTPComplete = otpValues.all { it.isNotEmpty() }
    val isWaiting = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }
    val time = remember { mutableIntStateOf(120) }

    val otpNoBack = stringResource(R.string.otp_no_back)
    BackHandler { makeToast(context, otpNoBack) }

    LaunchedEffect(isOTPComplete) {
        if (isOTPComplete) {
            focusManager.clearFocus()
            val inpMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inpMethodManager.apply {
                hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0)
            }
        }
    }

    LaunchedEffect(isWaiting.value) {
        while (time.intValue > 0) {
            delay(1000)
            time.intValue--
        }
        isWaiting.value = false
    }

    if (isLoading.value) {
        LoadingScreen()
    }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Spacing.md),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(BgPrimaryColor, CircleShape)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Confirm Icon",
                    tint = PrimaryColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xxl))

            HeadingTextComponent(value = stringResource(R.string.otp_prompt))

            Spacer(modifier = Modifier.height(Spacing.xl))

            Text(
                text = stringResource(R.string.otp_placeholder, otpMetadata.email),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp),
                style = TextStyle(
                    fontSize = 15.sp,
                    fontStyle = FontStyle.Normal,
                    lineHeight = 20.sp
                ),
                textAlign = TextAlign.Center,
                fontSize = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.Both
                    )
                ).fontSize
            )

            Spacer(modifier = Modifier.height(Spacing.xxl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                otpValues.forEachIndexed { index, value ->
                    var isFocused by remember { mutableStateOf(false) }

                    BasicTextField(
                        value = value,
                        onValueChange = { newValue ->
                            if (newValue.length <= 1) {
                                val newOtpValues = otpValues.toMutableList()
                                newOtpValues[index] = newValue
                                otpValues = newOtpValues

                                if (newValue.isNotEmpty() && index < 5) {
                                    scope.launch {
                                        delay(10)
                                        focusRequesters[index + 1].requestFocus()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .border(
                                width = 2.dp,
                                color = if (isFocused) PrimaryColor else Color.Gray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .focusRequester(focusRequesters[index])
                            .onFocusChanged {
                                isFocused = it.isFocused
                            }
                            .onKeyEvent {
                                if (it.nativeKeyEvent.keyCode == 67) {
                                    if (index > 0) {
                                        val newOtpValues = otpValues.toMutableList()
                                        newOtpValues[index] = ""
                                        otpValues = newOtpValues
                                        focusRequesters[index - 1].requestFocus()
                                    }
                                }
                                false
                            },
                        textStyle = TextStyles.heading.copy(
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { innerTextField() }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xxxl))

            CustomButton(
                value = stringResource(R.string.verify),
                color = ButtonColor,
                modifier = Modifier.padding(horizontal = Spacing.sm),
                rounded = 20.dp,
                enabled = isOTPComplete
            ) {
                isLoading.value = true
                appViewModel.verifyOTP(
                    otpMetadata.email,
                    otpValues.joinToString(""),
                    context = context,
                    onSuccess = {
                        isLoading.value = false
                        if (otpMetadata.withOrigin)
                            otpMetadata.withAuthEndpoint?.let {
                                if (otpMetadata.wipeBackStack) backStack.clear()
                                backStack.add(it)
                            } ?: backStack.removeAt(backStack.lastIndex)
                        else onSuccessNoOrigin()
                    },
                    onFailed = { msg ->
                        isLoading.value = false
                        makeToast(context, msg)
                    }
                )
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            val otpNotReceived = stringResource(R.string.otp_not_received)
            val otpResend = if (isWaiting.value)
                stringResource(R.string.otp_resend, time.intValue)
            else stringResource(R.string.otp_resend_2)
            val otpResendSuccess = stringResource(R.string.otp_resend_success)
            val otpResendFailed = stringResource(R.string.otp_req_failed)

            ClickableTextComponent(
                style = TextStyles.body,
                AnnotatedText.Plain("$otpNotReceived "),
                AnnotatedText.Clickable(
                    otpResend, SpanStyle(PrimaryColor)
                ) {
                    isLoading.value = true
                    if (!isWaiting.value) {
                        appViewModel.requestOTP(
                            otpMetadata.toRequest(OTPPurpose.VERIFY_EMAIL),
                            onSuccess = {
                                isLoading.value = false
                                makeToast(context, otpResendSuccess)
                            },
                            onFailed = {
                                isLoading.value = false
                                makeToast(context, otpResendFailed)
                            }
                        )
                    }
                }
            )
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
fun ForgotPasswordEntry(
    backStack: NavBackStack<NavKey>,
    appViewModel: AppViewModel
) {
    val context = LocalContext.current
    val errorDescription = stringResource(R.string.error)
    val noInternetDescription = stringResource(R.string.net_error)

    val isLoading = remember { mutableStateOf(false) }
    val email = TextFieldState("")
    val emailError = remember { mutableStateOf<Int?>(null) }
    val emailTouched = remember { mutableStateOf(false) }

    val isOnline by appViewModel.isOnline.collectAsStateWithLifecycle()

    val onEmailFocusChange: (Boolean) -> Unit = { focused ->
        if (!focused) {
            emailTouched.value = true
            emailError.value = emailValidator(email.text.toString())
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { email.text.toString() to emailTouched.value }
            .debounce(150)
            .collect { (email, touched) ->
                if (touched)
                    emailError.value = emailValidator(email)
            }
    }

    if (isLoading.value) {
        LoadingScreen()
    }

    Scaffold(
        containerColor = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(Spacing.md),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp)
        ) {
            IconButton(
                onClick = { backStack.removeAt(backStack.lastIndex) },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Sign in Screen"
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeadingTextComponent(value = stringResource(R.string.email_prompt))
            Spacer(modifier = Modifier.height(Spacing.lg))
            Text(
                text = stringResource(R.string.otp_description),
                modifier = Modifier
                    .fillMaxWidth(),
                style = TextStyles.body.copy(fontSize = 16.sp),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(Spacing.xl))
            IconTextInput(
                icon = Icons.Filled.Email,
                placeholder = stringResource(R.string.email_prompt),
                state = email,
                inputType = InputType.Email,
                error = emailError.value?.let { stringResource(it) },
                onFocusChange = onEmailFocusChange,
                readOnly = isLoading.value
            )
            Spacer(modifier = Modifier.height(Spacing.xxl))
            CustomButton(
                value = stringResource(R.string.cont),
                color = ButtonColor,
                rounded = 20.dp,
                enabled = !isLoading.value
            ) {
                val error = emailValidator(email.text.toString())
                emailError.value = error
                if (error == null) {
                    if (isOnline) {
                        isLoading.value = true
                        appViewModel.requestOTP(
                            OTPRequest(null, email.text.toString(), OTPPurpose.RESET_PASSWORD),
                            onSuccess = {
                                isLoading.value = false
                                backStack.add(
                                    NavRoute.Auth.OTP(
                                        null,
                                        email.text.toString(),
                                        true,
                                        NavRoute.Auth.Login,
                                        true
                                    )
                                )
                            },
                            onFailed = {
                                isLoading.value = false
                                makeToast(context, errorDescription)
                            }
                        )
                    } else makeToast(context, noInternetDescription)
                }
            }
        }
    }
}

fun emailValidator(email: String): Int? {
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$".toRegex()
    return when {
        email.isEmpty() -> R.string.email_empty_error
        !emailRegex.matches(email) -> R.string.email_format_error
        else -> null
    }
}

@Composable
fun ResetPasswordEntry() {}

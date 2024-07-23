package com.example.hapticapptest

import android.os.Bundle
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hapticapptest.ui.theme.BetterTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetterTheme {
                val snackbarHost = remember {
                    SnackbarHostState()
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        SnackbarHost(snackbarHost)
                    }
                ) { innerPadding ->
                    val coroutineScope = rememberCoroutineScope()

                    ScreenContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onError = { message ->
                            coroutineScope.launch {
                                snackbarHost.showSnackbar(message)
                            }
                        }
                    )
                }
            }
        }
    }
}

data class Vibration(val name: String, val value: Int, val api: Int)

val vibrationsList = listOf(
    Vibration("LONG_PRESS", HapticFeedbackConstants.LONG_PRESS, 3),
    Vibration("VIRTUAL_KEY", HapticFeedbackConstants.VIRTUAL_KEY, 5),
    Vibration("KEYBOARD_TAP", HapticFeedbackConstants.KEYBOARD_TAP, 8),
    Vibration("CLOCK_TICK", HapticFeedbackConstants.CLOCK_TICK, 21),
    Vibration("CONTEXT_CLICK", HapticFeedbackConstants.CONTEXT_CLICK, 23),
    Vibration("KEYBOARD_PRESS", HapticFeedbackConstants.KEYBOARD_PRESS, 27),
    Vibration("KEYBOARD_RELEASE", HapticFeedbackConstants.KEYBOARD_RELEASE, 27),
    Vibration("VIRTUAL_KEY_RELEASE", HapticFeedbackConstants.VIRTUAL_KEY_RELEASE, 27),
    Vibration("TEXT_HANDLE_MOVE", HapticFeedbackConstants.TEXT_HANDLE_MOVE, 27),
    Vibration("GESTURE_START", HapticFeedbackConstants.GESTURE_START, 30),
    Vibration("GESTURE_END", HapticFeedbackConstants.GESTURE_END, 30),
    Vibration("CONFIRM", HapticFeedbackConstants.CONFIRM, 30),
    Vibration("REJECT", HapticFeedbackConstants.REJECT, 30),
    Vibration("TOGGLE_ON", HapticFeedbackConstants.TOGGLE_ON, 34),
    Vibration("TOGGLE_OFF", HapticFeedbackConstants.TOGGLE_OFF, 34),
    Vibration("GESTURE_THRESHOLD_ACTIVATE", HapticFeedbackConstants.GESTURE_THRESHOLD_ACTIVATE, 34),
    Vibration("GESTURE_THRESHOLD_DEACTIVATE", HapticFeedbackConstants.GESTURE_THRESHOLD_DEACTIVATE, 34),
    Vibration("DRAG_START", HapticFeedbackConstants.DRAG_START, 34),
    Vibration("SEGMENT_TICK", HapticFeedbackConstants.SEGMENT_TICK, 34),
    Vibration("SEGMENT_FREQUENT_TICK", HapticFeedbackConstants.SEGMENT_FREQUENT_TICK, 34)
)

@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    onError: (message: String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val isEnabled = remember { mutableStateOf(false) }

        val isHaptic = LocalView.current.isHapticFeedbackEnabled
        Text(
            text = "Haptic is ${if (isHaptic) "enabled" else "disabled"}",
            color = if (isHaptic) Color.Black else Color.Red,
        )

        Spacer(modifier = Modifier.height(16.dp))

        val api = android.os.Build.VERSION.SDK_INT
        Text(text = "Your api level is $api")


        val selectedVibration = remember {
            mutableIntStateOf(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Switcher(
            checked = isEnabled.value,
            onCheckedChange = { isEnabled.value = it },
            hapticVibration = selectedVibration.intValue
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow {
            items(vibrationsList) { vibration ->
                SelectableVibration(
                    name = vibration.name,
                    isSelected = selectedVibration.intValue == vibration.value,
                    onClick = {
                        if (api >= vibration.api) {
                            selectedVibration.intValue = vibration.value
                        } else {
                            onError("Your api level is lower than required. You api is $api, required api is ${vibration.api}")
                        }
                    },
                    canBeSelected = api >= vibration.api,
                    api = vibration.api.toString()
                )
            }
        }
    }
}

@Composable
fun SelectableVibration(
    name: String,
    api: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    canBeSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .run {
                if (isSelected) {
                    border(2.dp, Color.Green, RoundedCornerShape(12.dp))
                } else this
            }
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(horizontal = 8.dp),
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )

        Text(
            text = "api - $api",
            modifier = Modifier.padding(top = 4.dp),
            color = if (canBeSelected) Color.Black else Color.Red,
            textAlign = TextAlign.Center,
            fontSize = 12.sp
        )
    }
}

@Composable
fun Switcher(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: ((Boolean) -> Unit) = {},
    enabled: Boolean = true,
    hapticVibration: Int
) {
    val view = LocalView.current

    Switch(
        checked = checked,
        onCheckedChange = {
            view.performHapticFeedback(hapticVibration)
            onCheckedChange(it)
        },
        enabled = enabled,
        colors = SwitchDefaults.colors(
            checkedThumbColor = BetterTheme.colors.backgroundPrimary,
            checkedTrackColor = BetterTheme.colors.backgroundPositive,
            uncheckedBorderColor = Color.Transparent,
            uncheckedThumbColor = BetterTheme.colors.backgroundPrimary,
            uncheckedTrackColor = BetterTheme.colors.backgroundTertiary,
            disabledCheckedThumbColor = BetterTheme.colors.backgroundSecondary,
            disabledCheckedTrackColor = BetterTheme.colors.backgroundTertiary,
            disabledUncheckedThumbColor = BetterTheme.colors.backgroundSecondary,
            disabledUncheckedTrackColor = BetterTheme.colors.backgroundTertiary,
        ),
        modifier = modifier
    )
}


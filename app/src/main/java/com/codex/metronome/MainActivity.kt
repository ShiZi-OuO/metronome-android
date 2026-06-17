package com.codex.metronome

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MetronomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val state by viewModel.state.collectAsStateWithLifecycle()
            MetronomeTheme(darkMode = state.darkMode) {
                SystemBarsEffect(darkMode = state.darkMode)
                MetronomeScreen(state = state, viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeFromBackground()
    }

    override fun onPause() {
        viewModel.pauseForBackground()
        super.onPause()
    }
}

private val Accent = Color(0xFFEB825A)
private val AccentDark = Color(0xFFD66C44)
private val Gold = Color(0xFFE9AB3B)
private val Green = Color(0xFF6BBA6B)

private data class AppPalette(
    val page: Color,
    val card: Color,
    val text: Color,
    val muted: Color,
    val rail: Color,
    val control: Color,
    val divider: Color,
    val glassBorder: Color,
    val softTint: Color,
    val cardHighlight: Color,
    val cardShadow: Color,
    val beatDot: Color,
    val beatDotBorder: Color,
)

@Composable
private fun palette(darkMode: Boolean): AppPalette = if (darkMode) {
    AppPalette(
        page = Color(0xFF282B3E),
        card = Color(0xFF313448),
        text = Color.White,
        muted = Color(0xFFD8DAEE),
        rail = Color(0xFF4D516A),
        control = Color(0x663B3F58),
        divider = Color(0x334E536D),
        glassBorder = Color(0x55FFFFFF),
        softTint = Color(0x443B405C),
        cardHighlight = Color(0x24FFFFFF),
        cardShadow = Color(0x44151725),
        beatDot = Color(0xFF727791),
        beatDotBorder = Color(0x33FFFFFF),
    )
} else {
    AppPalette(
        page = Color(0xFFF5F7FB),
        card = Color(0xEFFFFFFF),
        text = Color(0xFF21232F),
        muted = Color(0xFF5D6276),
        rail = Color(0xFFE6E8EF),
        control = Color(0xAEE9EDF6),
        divider = Color(0x8AD9DFEB),
        glassBorder = Color(0xDFFFFFFF),
        softTint = Color(0x88F5D8CB),
        cardHighlight = Color(0xBFFFFFFF),
        cardShadow = Color(0x1A6A748E),
        beatDot = Color(0xFFE9EDF6),
        beatDotBorder = Color(0x99FFFFFF),
    )
}

@Composable
private fun MetronomeTheme(darkMode: Boolean, content: @Composable () -> Unit) {
    val colors = palette(darkMode)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as ComponentActivity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.toArgb()
        window.navigationBarColor = Color.Transparent.toArgb()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isStatusBarContrastEnforced = false
            window.isNavigationBarContrastEnforced = false
        }
        WindowInsetsControllerCompat(window, view).apply {
            isAppearanceLightStatusBars = !darkMode
            isAppearanceLightNavigationBars = !darkMode
        }
    }
    val scheme = if (darkMode) {
        darkColorScheme(
            primary = Accent,
            secondary = Accent,
            background = colors.page,
            surface = colors.card,
            onPrimary = Color.White,
            onBackground = colors.text,
            onSurface = colors.text,
        )
    } else {
        lightColorScheme(
            primary = Accent,
            secondary = Accent,
            background = colors.page,
            surface = colors.card,
            onPrimary = Color.White,
            onBackground = colors.text,
            onSurface = colors.text,
        )
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
private fun SystemBarsEffect(darkMode: Boolean) {
    val view = LocalView.current
    val window = (view.context as ComponentActivity).window
    DisposableEffect(darkMode) {
        val controller = WindowInsetsControllerCompat(window, view)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        controller.show(WindowInsetsCompat.Type.systemBars())
        controller.isAppearanceLightStatusBars = !darkMode
        controller.isAppearanceLightNavigationBars = !darkMode
        onDispose {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

@Composable
private fun MetronomeScreen(state: MetronomeUiState, viewModel: MetronomeViewModel) {
    val colors = palette(state.darkMode)
    val density = LocalDensity.current
    val statusBarPadding = with(density) { WindowInsets.statusBars.getTop(this).toDp() }
    val navigationBarPadding = with(density) { WindowInsets.navigationBars.getBottom(this).toDp() }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MicaBackgroundBrush(state.darkMode)),
        ) {
            MicaTexture(darkMode = state.darkMode)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                colors.softTint,
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (state.darkMode) 0.08f else 0.72f),
                                Color.White.copy(alpha = if (state.darkMode) 0.02f else 0.08f),
                                Color.Transparent,
                            ),
                        ),
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                        .padding(
                            top = statusBarPadding + 10.dp,
                            bottom = navigationBarPadding + 14.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    MetronomeCard(state = state, viewModel = viewModel, colors = colors)
                    Spacer(Modifier.height(18.dp))
                }
            }
        }
    }
}

@Composable
private fun MicaBackgroundBrush(darkMode: Boolean): Brush = if (darkMode) {
    Brush.linearGradient(
        colors = listOf(
            Color(0xFF171A28),
            Color(0xFF30344C),
            Color(0xFF25293D),
            Color(0xFF11131D),
        ),
    )
} else {
    Brush.linearGradient(
        colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFEFF3FB),
            Color(0xFFFFE7DB),
            Color(0xFFE9EEF9),
        ),
    )
}

@Composable
private fun MicaTexture(darkMode: Boolean) {
    val lineColor = if (darkMode) Color.White.copy(alpha = 0.035f) else Color(0xFF6A748E).copy(alpha = 0.055f)
    val washColor = if (darkMode) Accent.copy(alpha = 0.10f) else Accent.copy(alpha = 0.16f)
    Canvas(modifier = Modifier.fillMaxSize()) {
        val step = 58.dp.toPx()
        var x = -size.height
        while (x < size.width + size.height) {
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(x, size.height),
                end = androidx.compose.ui.geometry.Offset(x + size.height, 0f),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round,
            )
            x += step
        }
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, washColor, Color.Transparent),
                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                end = androidx.compose.ui.geometry.Offset(size.width, size.height),
            ),
            alpha = 0.9f,
        )
    }
}

@Composable
private fun MetronomeCard(
    state: MetronomeUiState,
    viewModel: MetronomeViewModel,
    colors: AppPalette,
) {
    val shape = RoundedCornerShape(14.dp)
    Card(
        colors = CardDefaults.cardColors(containerColor = colors.card.copy(alpha = if (state.darkMode) 0.58f else 0.64f)),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (state.darkMode) 10.dp else 24.dp,
                shape = shape,
                ambientColor = colors.cardShadow,
                spotColor = colors.cardShadow,
            )
            .border(1.dp, colors.glassBorder, shape),
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.cardHighlight,
                            Color.White.copy(alpha = if (state.darkMode) 0.04f else 0.18f),
                            Color.Transparent,
                        ),
                    ),
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppTitle(colors)
            Spacer(Modifier.height(14.dp))
            TopModes(state, viewModel, colors)
            Spacer(Modifier.height(18.dp))
            BpmReadout(state, colors)
            Spacer(Modifier.height(8.dp))
            BpmSlider(state, viewModel, colors)
            Spacer(Modifier.height(20.dp))
            BeatDots(state, colors)
            Spacer(Modifier.height(18.dp))
            MainButtons(state, viewModel)
            Spacer(Modifier.height(20.dp))
            SettingsPanel(state, viewModel, colors)
        }
    }
}

@Composable
private fun AppTitle(colors: AppPalette) {
    Text(
        text = "为人类提供标准拍点服务",
        color = colors.text,
        fontSize = 19.sp,
        fontWeight = FontWeight.Black,
        lineHeight = 23.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun TopModes(state: MetronomeUiState, viewModel: MetronomeViewModel, colors: AppPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = state.darkMode,
                onCheckedChange = viewModel::setDarkMode,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF4D516D),
                    checkedTrackColor = Color(0x99A3A7C3),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0x99C1C1D8),
                    uncheckedBorderColor = Color.Transparent,
                ),
            )
            Spacer(Modifier.width(4.dp))
            Text("亮/暗模式", color = colors.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun BpmReadout(state: MetronomeUiState, colors: AppPalette) {
    Text("BPM", color = colors.muted, fontSize = 13.sp, fontWeight = FontWeight.Black)
    Text(
        text = state.bpm.toString(),
        color = colors.text,
        fontSize = 68.sp,
        fontWeight = FontWeight.Black,
        lineHeight = 70.sp,
    )
    Text(
        text = state.bpmName.ifBlank { " " },
        color = Accent,
        fontSize = 17.sp,
        fontWeight = FontWeight.Black,
        lineHeight = 20.sp,
        modifier = Modifier.height(22.dp),
    )
}

@Composable
private fun BpmSlider(state: MetronomeUiState, viewModel: MetronomeViewModel, colors: AppPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StepCircle(text = "-", color = Accent) { viewModel.changeBpm(-1) }
        Slider(
            value = state.bpm.toFloat(),
            onValueChange = { viewModel.setBpm(it.toInt()) },
            valueRange = MetronomeLogic.MinBpm.toFloat()..MetronomeLogic.MaxBpm.toFloat(),
            steps = MetronomeLogic.MaxBpm - MetronomeLogic.MinBpm - 1,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = colors.rail,
            ),
            modifier = Modifier.weight(1f),
        )
        StepCircle(text = "+", color = Accent) { viewModel.changeBpm(1) }
    }
}

@Composable
private fun StepCircle(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black, lineHeight = 28.sp)
    }
}

@Composable
private fun BeatDots(state: MetronomeUiState, colors: AppPalette) {
    Text("节拍", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Black)
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(state.beats) { index ->
            val active = state.activeBeat == index
            val color by animateColorAsState(
                targetValue = if (active) Accent else colors.beatDot,
                label = "beat-dot",
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (active) 24.dp else 20.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                        width = 1.dp,
                        color = if (active) Accent.copy(alpha = 0.55f) else colors.beatDotBorder,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

@Composable
private fun MainButtons(state: MetronomeUiState, viewModel: MetronomeViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Button(
            onClick = viewModel::toggleRunning,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = (if (state.isRunning) AccentDark else Accent).copy(alpha = 0.92f),
                contentColor = Color.White,
            ),
        ) {
            Text(if (state.isRunning) "停止" else "开始", fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        Button(
            onClick = { viewModel.tapBpm() },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Gold.copy(alpha = 0.92f), contentColor = Color.White),
        ) {
            Text("点击 BPM", fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun SettingsPanel(state: MetronomeUiState, viewModel: MetronomeViewModel, colors: AppPalette) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SectionDivider(colors)
        SettingRow(label = "节拍", colors = colors) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SmallStepperButton("-", colors) { viewModel.changeBeats(-1) }
                Text(
                    text = state.beats.toString(),
                    color = colors.text,
                    modifier = Modifier.size(width = 36.dp, height = 28.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                )
                SmallStepperButton("+", colors) { viewModel.changeBeats(1) }
            }
        }
        SectionDivider(colors)
        CheckLine(
            checked = state.stressFirstBeat,
            text = "压力第一拍",
            colors = colors,
            onChange = viewModel::setStressFirstBeat,
        )
        SectionDivider(colors)
        TimerLine(state, viewModel, colors)
        SectionDivider(colors)
        SubdivisionLine(state, viewModel, colors)
    }
}

@Composable
private fun SectionDivider(colors: AppPalette) {
    HorizontalDivider(color = colors.divider, thickness = 1.dp)
}

@Composable
private fun SettingRow(label: String, colors: AppPalette, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(34.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Black)
        content()
    }
}

@Composable
private fun SmallStepperButton(text: String, colors: AppPalette, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = colors.muted,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun CheckLine(
    checked: Boolean,
    text: String,
    colors: AppPalette,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Green,
                uncheckedColor = colors.rail,
                checkmarkColor = Color.White,
            ),
        )
        Text(text, color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TimerLine(state: MetronomeUiState, viewModel: MetronomeViewModel, colors: AppPalette) {
    val timerValue = if (state.isRunning) state.remainingTimerSeconds else state.timerDurationSeconds
    val (minutes, seconds) = MetronomeLogic.formatTimer(timerValue)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.timerEnabled,
                onCheckedChange = viewModel::setTimerEnabled,
                colors = CheckboxDefaults.colors(
                    checkedColor = Green,
                    uncheckedColor = colors.rail,
                    checkmarkColor = Color.White,
                ),
            )
            Text("定时器", color = colors.text, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            TimeField(
                value = minutes.toString(),
                enabled = !state.isRunning,
                colors = colors,
                onValueChange = viewModel::setTimerMinutes,
            )
            Text(
                ":",
                color = colors.muted,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 5.dp),
            )
            TimeField(
                value = seconds.toString().padStart(2, '0'),
                enabled = !state.isRunning,
                colors = colors,
                onValueChange = viewModel::setTimerSeconds,
            )
        }
    }
}

@Composable
private fun TimeField(
    value: String,
    enabled: Boolean,
    colors: AppPalette,
    onValueChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 54.dp, height = 44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(colors.control),
        contentAlignment = Alignment.Center,
    ) {
        BasicTextField(
            value = value,
            onValueChange = { next ->
                onValueChange(next.filter(Char::isDigit).take(2))
            },
            enabled = enabled,
            singleLine = true,
            textStyle = TextStyle(
                color = if (enabled) colors.text else colors.muted,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                lineHeight = 16.sp,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .width(42.dp)
                .height(20.dp),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SubdivisionLine(state: MetronomeUiState, viewModel: MetronomeViewModel, colors: AppPalette) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("细分", color = colors.text, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(10.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MetronomeLogic.SubdivisionPatterns.forEachIndexed { index, _ ->
                SubdivisionButton(
                    selected = state.subdivisionIndex == index,
                    index = index,
                    colors = colors,
                    onClick = { viewModel.setSubdivision(index) },
                )
            }
        }
    }
}

@Composable
private fun SubdivisionButton(
    selected: Boolean,
    index: Int,
    colors: AppPalette,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(7.dp)
    val background = if (selected) Accent.copy(alpha = 0.16f) else colors.control
    val iconColor = when {
        selected -> Accent
        colors.card == Color(0xFF313448) -> Color(0xFFD8DAEE)
        else -> Color.Black
    }
    val iconRes = when (index) {
        0 -> R.drawable.subdivision_1
        1 -> R.drawable.subdivision_2
        2 -> R.drawable.subdivision_3
        3 -> R.drawable.subdivision_4
        4 -> R.drawable.subdivision_5
        5 -> R.drawable.subdivision_6
        6 -> R.drawable.subdivision_7
        7 -> R.drawable.subdivision_8
        8 -> R.drawable.subdivision_9
        9 -> R.drawable.subdivision_10
        else -> R.drawable.subdivision_11
    }

    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 54.dp)
            .clip(shape)
            .background(background)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Accent else colors.glassBorder,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = "细分 ${index + 1}",
            colorFilter = ColorFilter.tint(iconColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp),
        )
    }
}

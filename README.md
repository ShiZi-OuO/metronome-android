# 人工节律研究所

> A Codex-assisted native Android metronome app for precise beat timing, rhythm subdivision, tap BPM, timer control, and mica-inspired light/dark UI.

人工节律研究所是一款原生 Android 节拍器 App，使用 Kotlin 与 Jetpack Compose 构建。项目复刻并重构在线节拍器工具页的核心交互，不使用 WebView，并将界面重新设计为云母材质风格。

本项目由开发者与 OpenAI Codex 协作完成，Codex 参与了工程搭建、Compose UI 实现、节拍器调度逻辑、测试补充、图标设计和 GitHub 发布整理。

## 功能

- BPM 范围 1..240，支持滑杆、加减按钮与点击 BPM。
- 拍数范围 1..12，支持每小节第一拍强拍。
- 11 种细分节奏模式，使用原网站语义对应的原生图标。
- 原生 SoundPool 节拍声音：普通拍、强拍、细分拍使用不同频率。
- 可选倒计时，计时结束自动停止。
- 亮色/暗色模式与设置持久化。
- 自适应应用图标。

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Android SoundPool
- SharedPreferences
- JUnit

## Codex 辅助开发

Codex 在本项目中主要用于：

- 搭建原生 Android / Gradle / Compose 工程结构。
- 实现 BPM、拍数、细分节奏、Tap BPM、计时器等核心逻辑。
- 设计云母材质 UI、亮暗模式、自适应图标和节奏细分图标适配。
- 编写并运行单元测试，验证 BPM 限制、拍数限制、Tap BPM、细分调度和计时器逻辑。

## 构建

要求：

- JDK 17
- Android SDK，建议 compile/target SDK 36

首次构建前，在项目根目录创建本机 `local.properties`：

```properties
sdk.dir=C:\\path\\to\\Android\\Sdk
```

构建 debug APK：

```bash
./gradlew :app:assembleDebug
```

APK 输出位置：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 测试

```bash
./gradlew :app:testDebugUnitTest
```

## 项目结构

```text
app/src/main/java/com/codex/metronome/
  MainActivity.kt              # Compose UI
  MetronomeViewModel.kt        # UI 状态与播放调度
  MetronomeLogic.kt            # BPM、拍数、细分、计时器核心逻辑
  MetronomeSoundPlayer.kt      # 原生节拍声音
  MetronomeSettingsStore.kt    # 设置持久化
  MetronomeUiState.kt          # 状态模型
```

## 许可证

本项目基于 [MIT License](LICENSE) 开源。

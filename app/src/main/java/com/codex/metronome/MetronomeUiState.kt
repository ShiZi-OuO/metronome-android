package com.codex.metronome

data class MetronomeUiState(
    val bpm: Int = MetronomeLogic.DefaultBpm,
    val beats: Int = MetronomeLogic.DefaultBeats,
    val activeBeat: Int? = null,
    val isRunning: Boolean = false,
    val stressFirstBeat: Boolean = false,
    val timerEnabled: Boolean = false,
    val timerDurationSeconds: Int = MetronomeLogic.DefaultTimerSeconds,
    val remainingTimerSeconds: Int = MetronomeLogic.DefaultTimerSeconds,
    val subdivisionIndex: Int = 0,
    val darkMode: Boolean = false,
    val fullscreen: Boolean = false,
) {
    val subdivisionPattern: String
        get() = MetronomeLogic.SubdivisionPatterns[subdivisionIndex]

    val subdivision: List<Int>
        get() = MetronomeLogic.parseSubdivision(subdivisionPattern)

    val bpmName: String
        get() = MetronomeLogic.bpmName(bpm)
}

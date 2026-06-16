package com.codex.metronome

import android.content.Context

class MetronomeSettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("metronome_settings", Context.MODE_PRIVATE)

    fun load(): MetronomeUiState = MetronomeUiState(
        bpm = prefs.getInt("bpm", MetronomeLogic.DefaultBpm),
        beats = prefs.getInt("beats", MetronomeLogic.DefaultBeats),
        stressFirstBeat = prefs.getBoolean("stress_first_beat", false),
        timerEnabled = prefs.getBoolean("timer_enabled", false),
        timerDurationSeconds = prefs.getInt("timer_duration_seconds", MetronomeLogic.DefaultTimerSeconds),
        remainingTimerSeconds = prefs.getInt("timer_duration_seconds", MetronomeLogic.DefaultTimerSeconds),
        subdivisionIndex = prefs.getInt("subdivision_index", 0)
            .coerceIn(MetronomeLogic.SubdivisionPatterns.indices),
        darkMode = prefs.getBoolean("dark_mode", false),
    )

    fun save(state: MetronomeUiState) {
        prefs.edit()
            .putInt("bpm", state.bpm)
            .putInt("beats", state.beats)
            .putBoolean("stress_first_beat", state.stressFirstBeat)
            .putBoolean("timer_enabled", state.timerEnabled)
            .putInt("timer_duration_seconds", state.timerDurationSeconds)
            .putInt("subdivision_index", state.subdivisionIndex)
            .putBoolean("dark_mode", state.darkMode)
            .apply()
    }
}

package com.codex.metronome

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MetronomeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsStore = MetronomeSettingsStore(application)
    private val soundPlayer = MetronomeSoundPlayer(application)
    private val _state = MutableStateFlow(settingsStore.load())

    val state: StateFlow<MetronomeUiState> = _state.asStateFlow()

    private var metronomeJob: Job? = null
    private var timerJob: Job? = null
    private var previousTapMillis: Long? = null

    fun changeBpm(delta: Int) {
        setBpm(_state.value.bpm + delta)
    }

    fun setBpm(value: Int) {
        updateAndSave {
            it.copy(bpm = MetronomeLogic.clampBpm(value))
        }
    }

    fun changeBeats(delta: Int) {
        updateAndSave {
            val next = MetronomeLogic.clampBeats(it.beats + delta)
            it.copy(beats = next, activeBeat = it.activeBeat?.coerceAtMost(next - 1))
        }
    }

    fun setStressFirstBeat(value: Boolean) {
        updateAndSave { it.copy(stressFirstBeat = value) }
    }

    fun setTimerEnabled(value: Boolean) {
        if (!value) {
            timerJob?.cancel()
        }
        updateAndSave {
            it.copy(
                timerEnabled = value,
                remainingTimerSeconds = it.timerDurationSeconds,
            )
        }
    }

    fun setTimerMinutes(value: String) {
        val minutes = value.toIntOrNull()?.coerceIn(0, 99) ?: 0
        val seconds = _state.value.timerDurationSeconds % 60
        setTimerDuration(minutes * 60 + seconds)
    }

    fun setTimerSeconds(value: String) {
        val minutes = _state.value.timerDurationSeconds / 60
        val seconds = value.toIntOrNull()?.coerceIn(0, 59) ?: 0
        setTimerDuration(minutes * 60 + seconds)
    }

    fun setSubdivision(index: Int) {
        updateAndSave {
            it.copy(subdivisionIndex = index.coerceIn(MetronomeLogic.SubdivisionPatterns.indices))
        }
    }

    fun setDarkMode(value: Boolean) {
        updateAndSave { it.copy(darkMode = value) }
    }

    fun toggleFullscreen() {
        _state.update { it.copy(fullscreen = !it.fullscreen) }
    }

    fun tapBpm(nowMillis: Long = System.currentTimeMillis()) {
        val computed = MetronomeLogic.tapBpm(previousTapMillis, nowMillis)
        previousTapMillis = nowMillis
        if (computed != null) {
            setBpm(computed)
        }
    }

    fun toggleRunning() {
        if (_state.value.isRunning) {
            stop()
        } else {
            start()
        }
    }

    fun start() {
        if (_state.value.isRunning) return
        _state.update {
            it.copy(
                isRunning = true,
                activeBeat = null,
                remainingTimerSeconds = it.timerDurationSeconds,
            )
        }
        startTimerIfNeeded()
        startMetronomeLoop()
    }

    fun stop() {
        metronomeJob?.cancel()
        timerJob?.cancel()
        metronomeJob = null
        timerJob = null
        _state.update {
            it.copy(
                isRunning = false,
                activeBeat = null,
                remainingTimerSeconds = it.timerDurationSeconds,
            )
        }
    }

    override fun onCleared() {
        stop()
        soundPlayer.release()
        super.onCleared()
    }

    private fun setTimerDuration(totalSeconds: Int) {
        updateAndSave {
            val safe = totalSeconds.coerceIn(1, 99 * 60 + 59)
            it.copy(timerDurationSeconds = safe, remainingTimerSeconds = safe)
        }
    }

    private fun startTimerIfNeeded() {
        if (!_state.value.timerEnabled) return
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _state.value.isRunning && _state.value.remainingTimerSeconds > 0) {
                delay(1_000L)
                val next = _state.value.remainingTimerSeconds - 1
                _state.update { it.copy(remainingTimerSeconds = next.coerceAtLeast(0)) }
                if (next <= 0) {
                    stop()
                }
            }
        }
    }

    private fun startMetronomeLoop() {
        metronomeJob?.cancel()
        metronomeJob = viewModelScope.launch {
            var subdivisionCursor = 0
            while (isActive && _state.value.isRunning) {
                val current = _state.value
                val pattern = current.subdivision
                if (subdivisionCursor >= pattern.size) {
                    subdivisionCursor = 0
                }
                val interval = MetronomeLogic.tickIntervalMillis(current.bpm, pattern.size)

                if (subdivisionCursor == 0) {
                    val nextBeat = if (current.activeBeat == null) {
                        0
                    } else {
                        (current.activeBeat + 1) % current.beats
                    }
                    val accent = current.stressFirstBeat && nextBeat == 0
                    if (accent) soundPlayer.playAccent() else soundPlayer.playCommon()
                    _state.update { it.copy(activeBeat = nextBeat) }
                } else if (pattern[subdivisionCursor] == 1) {
                    soundPlayer.playSubdivision()
                }

                delay(interval)
                subdivisionCursor = (subdivisionCursor + 1) % pattern.size
            }
        }
    }

    private fun updateAndSave(transform: (MetronomeUiState) -> MetronomeUiState) {
        _state.update { current ->
            transform(current).also(settingsStore::save)
        }
    }
}

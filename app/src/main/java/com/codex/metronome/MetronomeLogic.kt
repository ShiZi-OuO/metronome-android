package com.codex.metronome

import kotlin.math.ceil

object MetronomeLogic {
    const val MinBpm = 1
    const val MaxBpm = 240
    const val DefaultBpm = 120
    const val MinBeats = 1
    const val MaxBeats = 12
    const val DefaultBeats = 4
    const val DefaultTimerSeconds = 60

    val SubdivisionPatterns = listOf(
        "1",
        "1-1",
        "1-1-1",
        "1-1-1-1",
        "1-0-0-1",
        "1-1-0-0",
        "1-0-1-0-1-1",
        "1-1-1-0-1-0",
        "1-0-1-1-1-0",
        "1-0-0-1-1-1",
        "1-1-1-1-0-0",
    )

    private val bpmTitles = linkedMapOf(
        20 to "Larghissimo",
        40 to "Grave",
        45 to "Lento",
        50 to "Largo",
        60 to "Adagio",
        70 to "Adagietto",
        85 to "Andante",
        97 to "Moderato",
        109 to "Allegretto",
        132 to "Allegro",
        140 to "Vivace",
        177 to "Presto",
        240 to "Prestissimo",
    )

    fun clampBpm(value: Int): Int = value.coerceIn(MinBpm, MaxBpm)

    fun clampBeats(value: Int): Int = value.coerceIn(MinBeats, MaxBeats)

    fun parseSubdivision(pattern: String): List<Int> {
        val parsed = pattern.split("-").mapNotNull { it.toIntOrNull() }
        return if (parsed.isEmpty()) listOf(1) else parsed.map { it.coerceIn(0, 1) }
    }

    fun tickIntervalMillis(bpm: Int, subdivisionLength: Int): Long {
        val safeBpm = clampBpm(bpm)
        val safeSubdivision = subdivisionLength.coerceAtLeast(1)
        return (60_000.0 / safeBpm / safeSubdivision).toLong().coerceAtLeast(1L)
    }

    fun tapBpm(previousTapMillis: Long?, currentTapMillis: Long): Int? {
        if (previousTapMillis == null) return null
        val delta = currentTapMillis - previousTapMillis
        if (delta <= 0) return null
        return clampBpm(ceil(60_000.0 / delta).toInt())
    }

    fun bpmName(bpm: Int): String {
        val current = clampBpm(bpm)
        return bpmTitles.entries.firstOrNull { current < it.key }?.value.orEmpty()
    }

    fun formatTimer(totalSeconds: Int): Pair<Int, Int> {
        val safe = totalSeconds.coerceAtLeast(0)
        return safe / 60 to safe % 60
    }
}

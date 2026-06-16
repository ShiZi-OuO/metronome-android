package com.codex.metronome

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MetronomeLogicTest {
    @Test
    fun clampsBpm() {
        assertEquals(1, MetronomeLogic.clampBpm(-10))
        assertEquals(120, MetronomeLogic.clampBpm(120))
        assertEquals(240, MetronomeLogic.clampBpm(500))
    }

    @Test
    fun clampsBeats() {
        assertEquals(1, MetronomeLogic.clampBeats(0))
        assertEquals(4, MetronomeLogic.clampBeats(4))
        assertEquals(12, MetronomeLogic.clampBeats(40))
    }

    @Test
    fun calculatesTapBpm() {
        assertNull(MetronomeLogic.tapBpm(null, 1_000))
        assertEquals(120, MetronomeLogic.tapBpm(1_000, 1_500))
        assertEquals(240, MetronomeLogic.tapBpm(1_000, 1_100))
    }

    @Test
    fun parsesSubdivision() {
        assertEquals(listOf(1, 0, 0, 1), MetronomeLogic.parseSubdivision("1-0-0-1"))
        assertEquals(listOf(1), MetronomeLogic.parseSubdivision(""))
        assertEquals(listOf(1, 0), MetronomeLogic.parseSubdivision("5--0"))
    }

    @Test
    fun calculatesTickInterval() {
        assertEquals(500L, MetronomeLogic.tickIntervalMillis(120, 1))
        assertEquals(125L, MetronomeLogic.tickIntervalMillis(120, 4))
        assertEquals(250L, MetronomeLogic.tickIntervalMillis(240, 1))
    }

    @Test
    fun formatsTimer() {
        assertEquals(1 to 0, MetronomeLogic.formatTimer(60))
        assertEquals(1 to 5, MetronomeLogic.formatTimer(65))
        assertEquals(0 to 0, MetronomeLogic.formatTimer(-1))
    }
}

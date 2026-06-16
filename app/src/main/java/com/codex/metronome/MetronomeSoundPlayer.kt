package com.codex.metronome

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class MetronomeSoundPlayer(context: Context) {
    private val appContext = context.applicationContext
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var loaded = false
    private val commonSoundId: Int
    private val accentSoundId: Int
    private val subdivisionSoundId: Int

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loaded = true
        }
        commonSoundId = soundPool.load(writeClickFile("common_click.wav", 440.0, 0.7), 1)
        accentSoundId = soundPool.load(writeClickFile("accent_click.wav", 880.0, 0.9), 1)
        subdivisionSoundId = soundPool.load(writeClickFile("subdivision_click.wav", 520.0, 0.3), 1)
    }

    fun playCommon() = play(commonSoundId)

    fun playAccent() = play(accentSoundId)

    fun playSubdivision() = play(subdivisionSoundId)

    fun release() {
        soundPool.release()
    }

    private fun play(soundId: Int) {
        if (loaded) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    private fun writeClickFile(fileName: String, frequency: Double, gain: Double): String {
        val file = File(appContext.cacheDir, fileName)
        if (!file.exists()) {
            file.writeBytes(generateWav(frequency, gain))
        }
        return file.absolutePath
    }

    private fun generateWav(frequency: Double, gain: Double): ByteArray {
        val sampleRate = 44_100
        val sampleCount = (sampleRate * 0.1).toInt()
        val pcmBytes = ByteArray(sampleCount * 2)
        val buffer = ByteBuffer.wrap(pcmBytes).order(ByteOrder.LITTLE_ENDIAN)
        val base = 2.0 * PI / sampleRate * frequency
        val fastDecay = 100.0 / sampleRate
        val mediumDecay = 200.0 / sampleRate
        val slowDecay = 500.0 / sampleRate

        repeat(sampleCount) { index ->
            val value = gain * (
                0.09 * exp(-index * fastDecay) * sin(base * index) +
                    0.34 * exp(-index * mediumDecay) * sin(2.0 * base * index) +
                    0.57 * exp(-index * slowDecay) * sin(6.0 * base * index)
                )
            buffer.putShort((value.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort())
        }

        val dataSize = pcmBytes.size
        val wav = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)
        wav.put("RIFF".toByteArray())
        wav.putInt(36 + dataSize)
        wav.put("WAVE".toByteArray())
        wav.put("fmt ".toByteArray())
        wav.putInt(16)
        wav.putShort(1)
        wav.putShort(1)
        wav.putInt(sampleRate)
        wav.putInt(sampleRate * 2)
        wav.putShort(2)
        wav.putShort(16)
        wav.put("data".toByteArray())
        wav.putInt(dataSize)
        wav.put(pcmBytes)
        return wav.array()
    }
}

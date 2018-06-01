package io.github.pshegger.gamedevexperiments.algorithms.simplex

import java.util.*

/**
 * @author pshegger@gmail.com
 */
class SimplexNoise(largestFeature: Int, persistence: Double, seed: Int) {
    private val octavesCount = Math.ceil(Math.log10(largestFeature.toDouble()) / Math.log10(2.0)).toInt()

    private val rng = Random(seed.toLong())
    private val octaves = (0 until octavesCount).map { SimplexNoiseOctave(rng.nextInt()) }
    private val frequencies = (0 until octavesCount).map { i -> Math.pow(2.0, i.toDouble()) }
    private val amplitudes = (0 until octavesCount).map { i -> Math.pow(persistence, (octavesCount - i).toDouble()) }

    fun getNoise(x: Int, y: Int): Double = (0 until octavesCount).map { i -> octaves[i].noise(x / frequencies[i], y / frequencies[i]) * amplitudes[i] }.sum()
}
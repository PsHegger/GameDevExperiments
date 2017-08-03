package io.github.pshegger.gamedevexperiments

import android.graphics.Canvas
import android.graphics.Color

/**
 * @author gergely.hegedus@tappointment.com
 */
interface Scene {
    fun sizeChanged(width: Int, height: Int)
    fun update(deltaTime: Long)
    fun render(canvas: Canvas)
    fun onBackPressed()

    fun fpsColor(): Int = Color.BLACK
}
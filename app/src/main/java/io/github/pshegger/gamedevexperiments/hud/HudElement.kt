package io.github.pshegger.gamedevexperiments.hud

import android.graphics.Canvas
import io.github.pshegger.gamedevexperiments.utils.Touch

/**
 * @author gergely.hegedus@tappointment.com
 */
interface HudElement {
    fun update(deltaTime: Long, touch: Touch?)
    fun render(canvas: Canvas)
}
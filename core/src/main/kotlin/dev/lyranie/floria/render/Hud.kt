/*
 * Copyright (c) 2026 lyranie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package dev.lyranie.floria.render

import dev.lyranie.floria.render.screen.ClientHudScreen
import net.minecraft.client.gui.DrawContext

object Hud {
    var hudScreen: ClientHudScreen? = null

    private var lastWidth = -1
    private var lastHeight = -1

    fun init(drawContext: DrawContext) {
        if (hudScreen != null) return

        hudScreen = ClientHudScreen()
        hudScreen?.init(drawContext.scaledWindowWidth, drawContext.scaledWindowHeight)
    }

    fun draw(drawContext: DrawContext) {
        val currentWidth = drawContext.scaledWindowWidth
        val currentHeight = drawContext.scaledWindowHeight

        if (currentWidth != lastWidth || currentHeight != lastHeight) {
            hudScreen?.resize(currentWidth, currentHeight)
            lastWidth = currentWidth
            lastHeight = currentHeight
        }

        hudScreen?.render(drawContext, 0, 0, 0f)
    }
}

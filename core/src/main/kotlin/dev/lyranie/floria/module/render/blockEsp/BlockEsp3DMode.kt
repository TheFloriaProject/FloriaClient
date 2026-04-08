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

package dev.lyranie.floria.module.render.blockEsp

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientMode
import dev.lyranie.floria.event.events.WorldRenderEvent
import dev.lyranie.floria.module.render.BlockEspModule
import dev.lyranie.floria.util.RenderHelper
import net.minecraft.client.render.RenderLayers
import net.minecraft.util.math.Box
import org.lwjgl.opengl.GL11

class BlockEsp3DMode(
    private val module: BlockEspModule,
    private val radius: () -> Int,
    private val lineWidth: () -> Float,
    private val lineColor: () -> Int,
) : ClientMode() {
    override fun onEvent(event: ClientEvent) {
        if (event !is WorldRenderEvent) return

        val red = (lineColor.invoke() shr 16 and 0xFF) / 255f
        val green = (lineColor.invoke() shr 8 and 0xFF) / 255f
        val blue = (lineColor.invoke() and 0xFF) / 255f

        val cameraPos = event.camera.cameraPos
        val box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
        val buffer = event.consumers.getBuffer(RenderLayers.LINES)
        val matrices = event.matrices

        module.getBlocks(radius.invoke()).forEach { pos ->
            matrices.push()
            matrices.translate(
                (pos.x - cameraPos.x).toFloat(),
                (pos.y - cameraPos.y).toFloat(),
                (pos.z - cameraPos.z).toFloat()
            )

            RenderHelper.drawBox(
                matrices.peek().positionMatrix,
                buffer,
                box,
                red,
                green,
                blue,
                1.0f,
                lineWidth.invoke()
            )

            matrices.pop()
        }

        GL11.glDisable(GL11.GL_DEPTH_TEST)
        event.consumers.draw()
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }
}

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

package dev.lyranie.floria.util

import net.minecraft.client.render.VertexConsumer
import net.minecraft.util.math.Box
import org.joml.Matrix4f
import kotlin.math.sqrt

object RenderHelper {
    @Suppress("DUPLICATES")
    fun drawBox(
        matrix: Matrix4f?,
        buffer: VertexConsumer,
        box: Box,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        lineWidth: Float = 1f,
    ) {
        val minX = box.minX.toFloat()
        val minY = box.minY.toFloat()
        val minZ = box.minZ.toFloat()
        val maxX = box.maxX.toFloat()
        val maxY = box.maxY.toFloat()
        val maxZ = box.maxZ.toFloat()

        drawLine(matrix, buffer, minX, minY, minZ, maxX, minY, minZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, minY, minZ, maxX, minY, maxZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, minY, maxZ, minX, minY, maxZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, minX, minY, maxZ, minX, minY, minZ, r, g, b, a, lineWidth)

        drawLine(matrix, buffer, minX, maxY, minZ, maxX, maxY, minZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, maxY, minZ, maxX, maxY, maxZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, maxY, maxZ, minX, maxY, maxZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, minX, maxY, maxZ, minX, maxY, minZ, r, g, b, a, lineWidth)

        drawLine(matrix, buffer, minX, minY, minZ, minX, maxY, minZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, minY, minZ, maxX, maxY, minZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, maxX, minY, maxZ, maxX, maxY, maxZ, r, g, b, a, lineWidth)
        drawLine(matrix, buffer, minX, minY, maxZ, minX, maxY, maxZ, r, g, b, a, lineWidth)
    }

    fun drawLine(
        matrix: Matrix4f?,
        buffer: VertexConsumer,
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        r: Float,
        g: Float,
        b: Float,
        a: Float,
        lineWidth: Float,
    ) {
        val dx = x2 - x1
        val dy = y2 - y1
        val dz = z2 - z1
        val len = sqrt((dx * dx + dy * dy + dz * dz).toDouble()).toFloat()
        val nx = dx / len
        val ny = dy / len
        val nz = dz / len

        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a).normal(nx, ny, nz).light(100).lineWidth(lineWidth)
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a).normal(nx, ny, nz).light(100).lineWidth(lineWidth)
    }
}

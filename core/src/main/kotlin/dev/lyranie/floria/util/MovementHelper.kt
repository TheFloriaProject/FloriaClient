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

import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.math.Vec3d
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementHelper {
    fun getDirectionalVelocity(player: ClientPlayerEntity, speed: Double): Vec3d {
        val yaw = Math.toRadians(player.yaw.toDouble())
        val forward = player.input.movementInput.y.toDouble()
        val sideways = player.input.movementInput.x.toDouble()

        if (forward == 0.0 && sideways == 0.0) {
            return Vec3d(0.0, 0.0, 0.0)
        }

        val forwardX = -sin(yaw)
        val forwardZ = cos(yaw)
        val strafeX = cos(yaw)
        val strafeZ = sin(yaw)
        var x = forwardX * forward + strafeX * sideways
        var z = forwardZ * forward + strafeZ * sideways

        val length = sqrt(x * x + z * z)
        if (length > 0) {
            x = (x / length) * speed
            z = (z / length) * speed
        }

        return Vec3d(x, 0.0, z)
    }
}

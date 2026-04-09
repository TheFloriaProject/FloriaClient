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

package dev.lyranie.floria.module.movement

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.delegate.range
import dev.lyranie.floria.event.events.PlayerTickEvent

class SpiderModule : ClientModule("spider", ClientCategory.MOVEMENT) {
    private val speedSetting by range("speed", "Climb speed", 0.1, 1.0, 0.1, 0.1)

    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return

        val player = event.player
        val velocity = player.velocity
        val speed = speedSetting.value

        if (!player.horizontalCollision) return
        if (velocity.y >= speed) return

        player.setVelocity(velocity.x, speed, velocity.z)
    }
}

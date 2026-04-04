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
import dev.lyranie.floria.event.events.PlayerTickEvent

class BunnyHopModule : ClientModule("bunnyHop", ClientCategory.MOVEMENT) {
    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return

        val player = event.player

        if (player.input.movementInput.x == 0f && player.input.movementInput.y == 0f) return
        if (!player.isOnGround) return

        player.jump()
    }
}

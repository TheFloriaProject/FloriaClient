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

package dev.lyranie.floria.module.movement.fly

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientMode
import dev.lyranie.floria.event.events.PlayerTickEvent
import net.minecraft.client.MinecraftClient

class VanillaFlyMode : ClientMode() {
    private val client = MinecraftClient.getInstance()

    override fun onDisable() {
        client?.player?.let {
            it.abilities.flying = false
            it.sendAbilitiesUpdate()
        }
    }

    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return

        event.player.abilities.flying = true
        event.player.sendAbilitiesUpdate()
    }
}

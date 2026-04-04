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
import dev.lyranie.floria.util.MovementHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket

class VelocityFlyMode(private val velocity: () -> Double) : ClientMode() {
    private val client = MinecraftClient.getInstance()

    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return
        if (client == null) return

        var yVelocity = 0.0
        val speed = velocity.invoke()

        if (client.options!!.jumpKey.isPressed) yVelocity = speed
        if (client.options!!.sneakKey.isPressed) yVelocity = -speed

        val movement = MovementHelper.getDirectionalVelocity(event.player, speed)

        event.player.setVelocity(movement.x, yVelocity, movement.z)
        event.player.networkHandler.sendPacket(PlayerMoveC2SPacket.OnGroundOnly(true, event.player.horizontalCollision))
    }
}

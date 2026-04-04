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

package dev.lyranie.floria.module.network

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.event.events.PacketEvent
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket

class PausePacketsModule : ClientModule("pausePackets", ClientCategory.NETWORK) {
    private val packets = arrayListOf<Packet<*>>()

    override fun onEnable() {
        packets.clear()
    }

    override fun onDisable() {
        client?.player?.let { player ->
            packets.forEach(player.networkHandler::sendPacket)
        }
    }

    override fun onEvent(event: ClientEvent) {
        if (event !is PacketEvent) return
        if (event.packet is KeepAliveC2SPacket) return

        packets.add(event.packet)
        event.callbackInfo.cancel()
    }
}

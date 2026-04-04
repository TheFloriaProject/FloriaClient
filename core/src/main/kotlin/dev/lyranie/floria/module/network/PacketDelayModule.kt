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
import dev.lyranie.floria.api.setting.delegate.range
import dev.lyranie.floria.event.events.DisconnectEvent
import dev.lyranie.floria.event.events.PacketEvent
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.common.KeepAliveC2SPacket

class PacketDelayModule : ClientModule("packetDelay", ClientCategory.NETWORK) {
    private val delaySetting by range("delay", "Delay", 0.0, 1200.0, 1.0, 20.0)

    private val packets = arrayListOf<DelayedPacket>()
    private var sending = false

    private data class DelayedPacket(var delay: Double, val packet: Packet<*>)

    override fun onEnable() {
        packets.clear()
    }

    override fun onDisable() {
        client?.player?.let { player ->
            packets.map { it.packet }.forEach(player.networkHandler::sendPacket)
        }
    }

    override fun onEvent(event: ClientEvent) {
        if (event is DisconnectEvent) {
            packets.clear()
        }

        if (event !is PacketEvent) return
        if (event.packet is KeepAliveC2SPacket) return
        if (sending) return

        packets.add(DelayedPacket(delaySetting.value, event.packet))
        event.callbackInfo.cancel()

        val iterator = packets.iterator()
        while (iterator.hasNext()) {
            val packetDelay = iterator.next()
            if (packetDelay.delay <= 0) {
                iterator.remove()
                sending = true
                try {
                    client?.player?.networkHandler?.sendPacket(packetDelay.packet)
                } finally {
                    sending = false
                }
            } else {
                packetDelay.delay--
            }
        }
    }
}

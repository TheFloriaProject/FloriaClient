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

import dev.lyranie.floria.Floria
import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.delegate.bool
import dev.lyranie.floria.event.events.PacketEvent
import net.minecraft.text.Text

class PacketLoggerModule : ClientModule("packetLogger", ClientCategory.NETWORK) {
    private val logC2SPacketsSetting by bool("logServerBound", "Log Server bound", true)
    private val logS2CPacketsSetting by bool("logClientBound", "Log Client bound", true)
    private val logToChatSetting by bool("logChat", "Log to chat")

    private val logFilePath = Floria.SAVE_DIR.resolve("packets.log")
    private val logFile = logFilePath.toFile()

    override fun onEvent(event: ClientEvent) {
        if (event !is PacketEvent) return
        if (!logFile.exists()) logFile.createNewFile()

        if (event.type == PacketEvent.Type.INCOMING && !logS2CPacketsSetting.enabled) return
        if (event.type == PacketEvent.Type.OUTGOING && !logC2SPacketsSetting.enabled) return

        logFile.appendText("${formatPacket(event)}\n")

        if (!logToChatSetting.enabled) return

        client?.execute {
            client?.player?.sendMessage(Text.literal(formatPacket(event)), false)
        }
    }

    private fun formatPacket(event: PacketEvent) = StringBuilder().apply {
        val packet = event.packet

        append("[${event.type}] ")
        append("(${packet.packetType.id.path}) ")

        packet::class.java.declaredFields
            .filter { it.name != "CODEC" }
            .forEach { field ->
                field.isAccessible = true
                val value = runCatching { field.get(packet) }.getOrNull()
                val formatted = when (value) {
                    null -> "null"
                    is Boolean, is Number, is String, is Char -> value.toString()
                    is Enum<*> -> value.name
                    else -> value::class.java.simpleName
                }
                append("${field.name}=$formatted ")
            }
    }.toString()
}

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

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.mojang.serialization.JsonOps
import dev.lyranie.floria.Floria
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs

object ChatUtils {
    object Color {
        const val BG = 0xff38c5d8.toInt()
        const val PRIMARY = 0xfffe7f4f.toInt()
        const val SECONDARY = 0xffb43d1f.toInt()
    }

    fun sendMessage(player: ClientPlayerEntity, content: String = "", color: Int = -1) {
        player.sendMessage(Text.literal(content).withColor(color), false)
    }

    fun sendMessage(player: ClientPlayerEntity, content: Component) {
        val json = GsonComponentSerializer.gson().serialize(content)
        try {
            val text = TextCodecs.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow()
            player.sendMessage(text, false)
        } catch (e: JsonSyntaxException) {
            Floria.logger.error(e.message, e)
        }
    }
}

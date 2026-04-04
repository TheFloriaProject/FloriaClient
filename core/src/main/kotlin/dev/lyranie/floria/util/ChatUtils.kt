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
import net.minecraft.text.Text

object ChatUtils {
    object Color {
        const val BG = 0xff38c5d8.toInt()
        const val PRIMARY = 0xfffe7f4f.toInt()
        const val SECONDARY = 0xffb43d1f.toInt()
    }

    fun sendMessage(player: ClientPlayerEntity, content: String = "", color: Int = -1) {
        player.sendMessage(Text.literal(content).withColor(color), false)
    }
}

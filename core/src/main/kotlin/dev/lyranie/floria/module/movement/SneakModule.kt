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
import dev.lyranie.floria.api.setting.delegate.enum
import dev.lyranie.floria.module.movement.sneak.PacketSneakMode
import dev.lyranie.floria.module.movement.sneak.VanillaSneakMode

class SneakModule : ClientModule("sneak", ClientCategory.MOVEMENT) {
    private val typeSetting by enum<Type>("type", "Type")

    private val mode get() = modes[typeSetting.selected]!!
    private val modes = mapOf(
        Type.VANILLA to VanillaSneakMode(),
        Type.PACKET to PacketSneakMode()
    )

    private enum class Type {
        VANILLA,
        PACKET
    }

    override fun onEvent(event: ClientEvent) = mode.onEvent(event)
}

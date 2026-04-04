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
import dev.lyranie.floria.api.module.ClientMode
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.delegate.enum
import dev.lyranie.floria.api.setting.delegate.range
import dev.lyranie.floria.module.movement.fly.VanillaFlyMode
import dev.lyranie.floria.module.movement.fly.VelocityFlyMode

class FlyModule : ClientModule("fly", ClientCategory.MOVEMENT) {
    private val typeSetting by enum<Type>("type", "Type") {
        modes.forEach { it.value.onDisable() }
        mode.onEnable()
    }
    private val velocitySetting by range("velocity", "Velocity", 0.0, 20.0)

    private val mode: ClientMode get() = modes[typeSetting.selected]!!
    private val modes = mapOf(
        Type.VANILLA to VanillaFlyMode(),
        Type.VELOCITY to VelocityFlyMode { velocitySetting.value }
    )

    private enum class Type {
        VANILLA,
        VELOCITY
    }

    override fun onEnable() = mode.onEnable()
    override fun onDisable() = modes.forEach { it.value.onDisable() }
    override fun onEvent(event: ClientEvent) = mode.onEvent(event)
}

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

package dev.lyranie.floria.module.combat

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.delegate.range
import dev.lyranie.floria.event.events.PlayerTickEvent
import net.minecraft.text.Text

class AutoLeaveModule : ClientModule("autoLeave", ClientCategory.COMBAT) {
    private val thresholdSetting by range("threshold", "Threshold", 0.5, 19.5, 0.5, 17.0)

    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return
        if (event.player.health >= thresholdSetting.value) return

        client?.disconnect(Text.literal("Leaving World"))
    }
}

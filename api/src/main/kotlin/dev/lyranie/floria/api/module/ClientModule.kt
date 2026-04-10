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

package dev.lyranie.floria.api.module

import com.google.gson.annotations.Expose
import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.setting.ClientSetting
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

open class ClientModule(
    @Expose var id: String,
    @Expose var category: ClientCategory,
    @Expose var keybind: Int = -1,
    @Expose var toggled: Boolean = false
) {
    @Expose
    val settings = arrayListOf<ClientSetting>()

    val name: String?
        get() = Text.translatable("module.floria.$id").string
    val description: String?
        get() = Text.translatable("description.floria.$id").string

    protected val client: MinecraftClient? = MinecraftClient.getInstance()

    open fun onEnable() {
    }

    open fun onDisable() {
    }

    open fun onEvent(event: ClientEvent) {
    }

    fun toggle() {
        toggled = !toggled
        if (toggled) {
            onEnable()
        } else {
            onDisable()
        }
    }
}

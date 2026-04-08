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

package dev.lyranie.floria.api.setting.delegate

import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.settings.ColorSetting
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ColorSettingDelegate(
    val id: String,
    val name: String,
    val default: Int,
    val onChange: (Int) -> Unit,
) : ReadOnlyProperty<ClientModule, ColorSetting> {
    private lateinit var setting: ColorSetting

    operator fun provideDelegate(thisRef: ClientModule, property: KProperty<*>): ColorSettingDelegate {
        setting = ColorSetting(id, name, default, onChange).also { thisRef.settings.add(it) }
        return this
    }

    override fun getValue(thisRef: ClientModule, property: KProperty<*>): ColorSetting = setting
}

fun color(id: String, name: String, default: Int = 0xffffff, onChange: (Int) -> Unit = {}) =
    ColorSettingDelegate(id, name, default, onChange)

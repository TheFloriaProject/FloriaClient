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
import dev.lyranie.floria.api.setting.settings.BooleanSetting
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class BooleanSettingDelegate(
    val id: String,
    val name: String,
    val default: Boolean,
) : ReadOnlyProperty<ClientModule, BooleanSetting> {
    private lateinit var setting: BooleanSetting

    operator fun provideDelegate(thisRef: ClientModule, property: KProperty<*>): BooleanSettingDelegate {
        setting = BooleanSetting(id, name, default).also { thisRef.settings.add(it) }
        return this
    }

    override fun getValue(thisRef: ClientModule, property: KProperty<*>): BooleanSetting = setting
}

fun bool(id: String, name: String, default: Boolean = false) = BooleanSettingDelegate(id, name, default)

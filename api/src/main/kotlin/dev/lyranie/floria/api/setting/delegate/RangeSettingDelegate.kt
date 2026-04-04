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
import dev.lyranie.floria.api.setting.settings.RangeSetting
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RangeSettingDelegate(
    val id: String,
    val name: String,
    val min: Double,
    val max: Double,
    val step: Double,
    val default: Double,
    val onChange: (Double) -> Unit,
) : ReadOnlyProperty<ClientModule, RangeSetting> {
    private lateinit var setting: RangeSetting

    operator fun provideDelegate(thisRef: ClientModule, property: KProperty<*>): RangeSettingDelegate {
        setting = RangeSetting(id, name, min, max, step, default, onChange).also { thisRef.settings.add(it) }
        return this
    }

    override fun getValue(thisRef: ClientModule, property: KProperty<*>): RangeSetting = setting
}

fun range(
    id: String,
    name: String,
    min: Double,
    max: Double,
    step: Double = 0.1,
    default: Double = 1.0,
    onChange: (Double) -> Unit = {},
) = RangeSettingDelegate(id, name, min, max, step, default, onChange)

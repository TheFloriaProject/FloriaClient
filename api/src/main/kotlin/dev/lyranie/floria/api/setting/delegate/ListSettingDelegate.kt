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
import dev.lyranie.floria.api.setting.settings.ListSetting
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ListSettingDelegate<T>(
    val id: String,
    val name: String,
    val items: MutableList<T>,
    val selected: MutableList<T>,
    val onChange: (List<T>) -> Unit,
) : ReadOnlyProperty<ClientModule, ListSetting<T>> {
    private lateinit var setting: ListSetting<T>

    operator fun provideDelegate(thisRef: ClientModule, property: KProperty<*>): ListSettingDelegate<T> {
        setting = ListSetting(id, name, items, selected, onChange).also { thisRef.settings.add(it) }
        return this
    }

    override fun getValue(thisRef: ClientModule, property: KProperty<*>): ListSetting<T> = setting
}

inline fun <reified T> list(
    id: String,
    name: String,
    items: MutableList<T>,
    selected: MutableList<T> = arrayListOf(),
    noinline onChange: (List<T>) -> Unit = {},
) = ListSettingDelegate(id, name, items, selected, onChange)

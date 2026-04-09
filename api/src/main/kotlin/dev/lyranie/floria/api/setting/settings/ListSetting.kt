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

package dev.lyranie.floria.api.setting.settings

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import dev.lyranie.floria.api.setting.ClientSetting
import dev.lyranie.floria.api.setting.SettingType

class ListSetting<T>(
    id: String,
    name: String,
    val items: MutableList<T>,
    @Expose var selected: MutableList<T>,
    val onChange: (List<T>) -> Unit,
) : ClientSetting(
    id,
    name,
    SettingType.LIST
) {
    fun add(value: T) {
        selected.add(value)
        onChange.invoke(selected)
    }

    fun remove(value: T) {
        selected.remove(value)
        onChange.invoke(selected)
    }

    @Suppress("UNCHECKED_CAST")
    override fun fromJson(json: JsonObject) {
        selected.addAll(json["items"]?.asJsonArray?.toList() as? List<T> ?: emptyList())
    }
}

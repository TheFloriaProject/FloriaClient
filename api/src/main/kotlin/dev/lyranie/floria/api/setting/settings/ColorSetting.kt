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

class ColorSetting(
    id: String,
    name: String,
    @Expose var color: Int = 0xffffff,
    val onChange: (Int) -> Unit,
) : ClientSetting(
    id,
    name,
    SettingType.COLOR
) {
    fun set(value: Int) {
        color = value
        onChange.invoke(color)
    }

    override fun fromJson(json: JsonObject) {
        color = json["color"].asInt
    }
}

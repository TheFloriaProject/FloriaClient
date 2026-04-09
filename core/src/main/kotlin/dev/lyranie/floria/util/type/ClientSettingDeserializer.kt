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

package dev.lyranie.floria.util.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import dev.lyranie.floria.api.setting.ClientSetting
import dev.lyranie.floria.api.setting.SettingType
import dev.lyranie.floria.api.setting.settings.BooleanSetting
import dev.lyranie.floria.api.setting.settings.ColorSetting
import dev.lyranie.floria.api.setting.settings.EnumSetting
import dev.lyranie.floria.api.setting.settings.ListSetting
import dev.lyranie.floria.api.setting.settings.RangeSetting
import java.lang.reflect.Type

class ClientSettingDeserializer : JsonDeserializer<ClientSetting> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ClientSetting? {
        val `object` = json?.asJsonObject
        val type = `object`?.get("type")?.asString
            ?.let { runCatching { SettingType.valueOf(it) }.getOrNull() }
            ?: throw JsonParseException("Missing or invalid setting type: ${`object`?.get("type")}")

        return when (type) {
            SettingType.BOOLEAN -> context?.deserialize(json, BooleanSetting::class.java)
            SettingType.RANGE -> context?.deserialize(json, RangeSetting::class.java)
            SettingType.ENUM -> context?.deserialize(json, EnumSetting::class.java)
            SettingType.COLOR -> context?.deserialize(json, ColorSetting::class.java)
            SettingType.LIST -> context?.deserialize(json, ListSetting::class.java)
        }
    }
}

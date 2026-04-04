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
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.category.CategoryManager
import dev.lyranie.floria.module.ModuleManager
import java.lang.reflect.Type

class ClientModuleAdapter : JsonSerializer<ClientModule>, JsonDeserializer<ClientModule> {
    override fun serialize(src: ClientModule?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonNull.INSTANCE

        return JsonObject().apply {
            addProperty("id", src.id)
            addProperty("toggled", src.toggled)
            addProperty("category", src.category.id)
            addProperty("keybind", src.keybind)
            add("settings", context?.serialize(src.settings))
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ClientModule? {
        val obj = json?.asJsonObject!!
        val id = obj.get("id")?.asString
        val module = ModuleManager.modules.firstOrNull { it.id == id } ?: return null

        obj.get("toggled")?.asBoolean?.let { module.toggled = it }
        obj.get("keybind")?.asInt?.let { module.keybind = it }
        obj.get("category")?.asString?.let {
            module.category = CategoryManager.categories.first { category -> category.id == it }!!
        }

        obj.getAsJsonArray("settings")?.forEach { settingEl ->
            val settingObj = settingEl.asJsonObject
            val settingId = settingObj.get("id")?.asString ?: return@forEach
            val existing = module.settings.firstOrNull { it.id == settingId } ?: return@forEach

            existing.fromJson(settingObj)
        }

        return module
    }
}

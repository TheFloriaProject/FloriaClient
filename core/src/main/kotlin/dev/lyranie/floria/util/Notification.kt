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

package dev.lyranie.floria.util

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.lyranie.floria.network.SseRegistry

private enum class NotificationType {
    INFO,
    WARNING,
    ERROR
}

fun info(title: String, description: String? = null) = notify(title, description, NotificationType.INFO)
fun warning(title: String, description: String? = null) = notify(title, description, NotificationType.WARNING)
fun error(title: String, description: String? = null) = notify(title, description, NotificationType.ERROR)

private fun notify(title: String, description: String?, type: NotificationType) {
    val data = JsonObject().apply {
        addProperty("type", type.name)
        addProperty("title", title)
        addProperty("description", description)
    }

    SseRegistry.push("notification", Gson().toJson(data))
}

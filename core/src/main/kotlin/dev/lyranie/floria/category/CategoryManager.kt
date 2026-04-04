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

package dev.lyranie.floria.category

import dev.lyranie.floria.Floria
import dev.lyranie.floria.api.FloriaAddon
import dev.lyranie.floria.api.module.ClientCategory
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object CategoryManager {
    private val SAVE_FILE = Floria.SAVE_DIR.resolve("categories.json")

    val categories = CopyOnWriteArrayList<ClientCategory>()

    fun registerAddon(addon: FloriaAddon) {
        val addonCategories = arrayListOf<ClientCategory>()

        addon.registerCategories(addonCategories)
        categories.addAll(addonCategories)
    }

    fun init() {
        registerBuiltins()

        if (SAVE_FILE.notExists()) save()
    }

    fun save() {
        SAVE_FILE.writeText(Floria.gson.toJson(categories))
    }

    fun load() {
        val saved = SAVE_FILE.readText()
        val loaded = Floria.gson.fromJson(saved, Array<ClientCategory>::class.java)

        categories.clear()
        categories.addAll(loaded)
    }

    private fun registerBuiltins() {
        categories.add(ClientCategory.COMBAT)
        categories.add(ClientCategory.EXPLOIT)
        categories.add(ClientCategory.MISC)
        categories.add(ClientCategory.MOVEMENT)
        categories.add(ClientCategory.NETWORK)
        categories.add(ClientCategory.RENDER)
    }
}

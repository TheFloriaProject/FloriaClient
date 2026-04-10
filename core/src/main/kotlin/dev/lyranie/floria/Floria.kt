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

package dev.lyranie.floria

import com.google.gson.GsonBuilder
import dev.lyranie.floria.api.FloriaAddon
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.ClientSetting
import dev.lyranie.floria.category.CategoryManager
import dev.lyranie.floria.command.CommandManager
import dev.lyranie.floria.module.ModuleManager
import dev.lyranie.floria.network.GuiServer
import dev.lyranie.floria.util.type.ClientModuleAdapter
import dev.lyranie.floria.util.type.ClientSettingDeserializer
import dev.lyranie.floria.util.type.TextAdapter
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists

class Floria : ModInitializer {
    companion object {
        const val MOD_ID = "floria"
        const val NAME = "Floria"
        const val VERSION = "0.2.0"
        const val TITLE = "$NAME v$VERSION"

        val logger: Logger = LogManager.getLogger(NAME)
        val SAVE_DIR: Path = FabricLoader.getInstance().configDir.resolve(MOD_ID)

        private val gsonBuilder = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(ClientSetting::class.java, ClientSettingDeserializer())
            .registerTypeAdapter(ClientModule::class.java, ClientModuleAdapter())
            .registerTypeAdapter(Text::class.java, TextAdapter())

        val globalGson = gsonBuilder.create()!!
        val gson = gsonBuilder
            .excludeFieldsWithoutExposeAnnotation()
            .create()!!
        val miniMessage = MiniMessage.miniMessage()

        fun stop(client: MinecraftClient) {
            client.scheduleStop()
            GuiServer.stop()
//            AccountManager.save()
            ModuleManager.save()
            CategoryManager.save()
        }
    }

    object Link {
        const val DISCORD = "https://discord.gg/QSnzrj2FPt"
        const val GITHUB = "https://github.com/TheFloriaProject"
    }

    override fun onInitialize() {
        if (SAVE_DIR.notExists()) SAVE_DIR.createDirectory()

        val loader = FabricLoader.getInstance()
        val addons = loader.getEntrypoints(MOD_ID, FloriaAddon::class.java)

        for (addon in addons) {
            logger.info("Found addon: ${addon.javaClass.simpleName}")

            CategoryManager.registerAddon(addon)
            ModuleManager.registerAddon(addon)
            CommandManager.registerAddon(addon)
        }
    }
}

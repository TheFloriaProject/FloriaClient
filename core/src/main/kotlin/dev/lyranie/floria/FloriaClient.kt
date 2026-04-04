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

import dev.lyranie.floria.category.CategoryManager
import dev.lyranie.floria.command.CommandManager
import dev.lyranie.floria.module.ModuleManager
import dev.lyranie.floria.network.GuiServer
import dev.lyranie.floria.render.Hud
import dev.lyranie.floria.render.screen.ClientClickGuiScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class FloriaClient : ClientModInitializer {
    override fun onInitializeClient() {
        Floria.logger.info("Floria Client initialized")

        GuiServer.start()

//        AccountManager.load()

        CategoryManager.init()
        CategoryManager.load()

        ModuleManager.init()
        ModuleManager.load()
        ModuleManager.save()

        CommandManager.init()

        HudElementRegistry.addLast(Identifier.of(Floria.MOD_ID, "hud")) { drawContext, _ ->
            Hud.init(drawContext)
            Hud.draw(drawContext)
        }

        val clickGuiKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.${Floria.MOD_ID}.gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                KeyBinding.Category.create(Identifier.of(Floria.MOD_ID, "main"))
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (clickGuiKey.wasPressed()) {
                client.setScreen(ClientClickGuiScreen())
            }
        }

        ClientLifecycleEvents.CLIENT_STOPPING.register(
            ClientLifecycleEvents.ClientStopping { client ->
                Floria.stop(client)
            }
        )
    }
}

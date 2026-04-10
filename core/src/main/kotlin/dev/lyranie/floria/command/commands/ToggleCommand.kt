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

package dev.lyranie.floria.command.commands

import dev.lyranie.floria.Floria
import dev.lyranie.floria.api.command.ClientCommand
import dev.lyranie.floria.module.ModuleManager
import dev.lyranie.floria.util.ChatUtils
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.text.Text

class ToggleCommand : ClientCommand("toggle") {
    companion object {
        private val USAGE_MESSAGE by lazy { Text.translatable("message.${Floria.MOD_ID}.command.toggle.usage").string }
        private val SUCCESS_MESSAGE by lazy {
            Text.translatable("message.${Floria.MOD_ID}.command.toggle.success").string
        }
        private val FAILURE_MESSAGE by lazy {
            Text.translatable("message.${Floria.MOD_ID}.command.toggle.failure").string
        }
    }

    override fun execute(player: ClientPlayerEntity, args: Array<String>) {
        if (args.size != 1) {
            val message = Floria.miniMessage.deserialize(USAGE_MESSAGE.format(".$name"))

            ChatUtils.sendMessage(player, message)
            return
        }

        val moduleId = args[0]
        val module = ModuleManager.modules.firstOrNull { it.id == moduleId }

        if (module == null) {
            val message = Floria.miniMessage.deserialize(FAILURE_MESSAGE.format(moduleId))

            ChatUtils.sendMessage(player, message)
            return
        }

        module.let(ModuleManager::toggleModule)

        val status = if (module.toggled) {
            "enabled"
        } else {
            "disabled"
        }
        val message = Floria.miniMessage.deserialize(SUCCESS_MESSAGE.format(moduleId, module.name, status))

        ChatUtils.sendMessage(player, message)
    }

    override fun complete(args: Array<String>): List<String> {
        if (args.size == 1) {
            return ModuleManager.modules.map { it.id }
        }
        return emptyList()
    }
}

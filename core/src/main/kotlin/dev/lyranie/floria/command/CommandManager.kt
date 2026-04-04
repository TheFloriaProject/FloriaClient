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

package dev.lyranie.floria.command

import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.lyranie.floria.Floria
import dev.lyranie.floria.api.FloriaAddon
import dev.lyranie.floria.api.command.ClientCommand
import dev.lyranie.floria.command.commands.ToggleCommand
import dev.lyranie.floria.mixin.accessor.ChatInputSuggestorAccessor
import dev.lyranie.floria.util.ChatUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ChatInputSuggestor
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.concurrent.CopyOnWriteArrayList

object CommandManager {
    val commands = CopyOnWriteArrayList<ClientCommand>()

    fun init() {
        registerBuiltins()
    }

    fun registerBuiltins() {
        commands.add(ToggleCommand())
    }

    fun registerAddon(addon: FloriaAddon) {
        val addonCommands = arrayListOf<ClientCommand>()

        addon.registerCommands(addonCommands)
        commands.addAll(addonCommands)
    }

    fun handleSuggestion(text: String, chatInputSuggestor: ChatInputSuggestor, callbackInfo: CallbackInfo) {
        if (!text.startsWith(".")) return

        callbackInfo.cancel()

        val partialCommand = text.substring(1)
        val parts = partialCommand.split(" ")
        val commandName = parts[0]
        val args = parts.drop(1).toTypedArray()
        val command = commands.firstOrNull { it.name == commandName }
        val lastArg = if (parts.size == 1) {
            commandName
        } else {
            args.lastOrNull() ?: ""
        }
        val startIndex = text.length - lastArg.length
        val builder = SuggestionsBuilder(text, startIndex)

        val suggestions = when {
            parts.size == 1 -> commands.filter { it.name.startsWith(commandName) }.map { it.name }
            command != null -> command.complete(args)
            else -> emptyList()
        }

        suggestions.isNotEmpty().let {
            suggestions.forEach(builder::suggest)

            (chatInputSuggestor as ChatInputSuggestorAccessor)
                .setPendingSuggestions(builder.buildFuture())
            chatInputSuggestor.show(false)
        }
    }

    fun handleCommand(content: String, callbackInfo: CallbackInfo) {
        if (!content.startsWith(".")) return

        callbackInfo.cancel()

        val player = MinecraftClient.getInstance().player ?: return
        val parts = content.split(" ")
        val commandName = parts[0]
        val args = parts.drop(1).toTypedArray()
        val command = commands.firstOrNull { it.name == commandName.substring(1) }

        if (command == null) {
            val translation = Text.translatable("message.${Floria.MOD_ID}.unknown_command").string
            val messageParts = translation.split("|")
            val message = Text.literal(messageParts[0])
                .append(Text.literal(commandName).withColor(ChatUtils.Color.BG))

            player.sendMessage(message, false)
            return
        }

        command.execute(player, args)
    }
}

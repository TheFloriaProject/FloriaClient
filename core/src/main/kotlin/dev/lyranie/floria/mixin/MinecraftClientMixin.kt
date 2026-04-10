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

package dev.lyranie.floria.mixin

import dev.lyranie.floria.Floria
import dev.lyranie.floria.event.EventHandler
import dev.lyranie.floria.event.events.ClientTickEvent
import dev.lyranie.floria.event.events.DisconnectEvent
import dev.lyranie.floria.event.events.SetScreenEvent
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(MinecraftClient::class)
class MinecraftClientMixin {
    @Inject(method = ["disconnect"], at = [At("TAIL")])
    fun disconnect(reason: Text, callbackInfo: CallbackInfo) {
        EventHandler.handleEvent(DisconnectEvent(reason, callbackInfo))
    }

    @Inject(method = ["setScreen"], at = [At("HEAD")], cancellable = true)
    fun setScreen(screen: Screen?, callbackInfo: CallbackInfo) {
        EventHandler.handleEvent(SetScreenEvent(screen, callbackInfo))
    }

    @Inject(method = ["getWindowTitle"], at = [At("HEAD")], cancellable = true)
    fun getWindowTitle(callbackInfo: CallbackInfoReturnable<String>) {
        callbackInfo.returnValue = Floria.TITLE
    }

    @Inject(method = ["tick"], at = [At("TAIL")])
    fun tick(callbackInfo: CallbackInfo) {
        EventHandler.handleEvent(ClientTickEvent(callbackInfo))
    }
}

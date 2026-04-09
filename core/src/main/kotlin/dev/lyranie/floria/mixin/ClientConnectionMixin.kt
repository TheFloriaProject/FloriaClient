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

import dev.lyranie.floria.event.EventHandler
import dev.lyranie.floria.event.events.PacketEvent
import io.netty.channel.ChannelFutureListener
import net.minecraft.network.ClientConnection
import net.minecraft.network.packet.Packet
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ClientConnection::class)
class ClientConnectionMixin {
    @Suppress("UNUSED_PARAMETER")
    @Inject(method = ["sendImmediately"], at = [At("TAIL")], cancellable = true)
    fun sendImmediately(
        packet: Packet<*>,
        listener: ChannelFutureListener?,
        flush: Boolean,
        callbackInfo: CallbackInfo,
    ) {
        EventHandler.handleEvent(PacketEvent(packet, PacketEvent.Type.OUTGOING, callbackInfo))
    }
}

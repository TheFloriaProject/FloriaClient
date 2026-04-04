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

package dev.lyranie.floria.network

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import java.util.concurrent.CopyOnWriteArrayList

object SseRegistry {
    private val clients = CopyOnWriteArrayList<ChannelHandlerContext>()

    fun add(ctx: ChannelHandlerContext) {
        clients.add(ctx)
        ctx.channel().closeFuture().addListener { clients.remove(ctx) }
    }

    fun contains(ctx: ChannelHandlerContext) = clients.contains(ctx)

    fun push(event: String, data: String = "") {
        val payload = "event: $event\ndata: $data\n\n"
        val dead = mutableListOf<ChannelHandlerContext>()
        for (ctx in clients) {
            if (ctx.channel().isActive) {
                ctx.writeAndFlush(Unpooled.copiedBuffer(payload, Charsets.UTF_8))
            } else {
                dead.add(ctx)
            }
        }
        clients.removeAll(dead.toSet())
    }
}

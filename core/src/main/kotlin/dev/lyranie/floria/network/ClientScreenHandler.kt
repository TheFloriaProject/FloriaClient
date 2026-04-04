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

import dev.lyranie.floria.Floria
import dev.lyranie.floria.render.ClientScreen
import dev.lyranie.floria.render.overlay.Hud
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion
import net.minecraft.client.MinecraftClient

class ClientScreenHandler : SimpleChannelInboundHandler<FullHttpRequest>() {
    override fun channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
        val uri = msg.uri().substringBefore("?")

        when {
            uri == "/api/event" -> {
                handleEvent(ctx)
            }

            uri.startsWith("/api/") -> {
                handleAction(ctx, uri, msg)
            }

            else -> {
                val clean = uri.trimEnd('/')
                val path = when {
                    uri == "/" || uri.isBlank() -> "/index.html"
                    uri.contains(".") -> uri
                    else -> "$clean/index.html"
                }

                val resourcePath = "/assets/${Floria.MOD_ID}/screen$path"
                val stream = javaClass.getResourceAsStream(resourcePath)

                if (stream == null) {
                    NetworkUtil.reply(ctx, HttpResponseStatus.NOT_FOUND, "text/plain", "Not found: $path")
                    return
                }

                val bytes = stream.use { it.readBytes() }

                NetworkUtil.reply(ctx, HttpResponseStatus.OK, NetworkUtil.mimeType(resourcePath), bytes)
            }
        }
    }

    private fun handleEvent(ctx: ChannelHandlerContext) {
        val response = DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

        response.headers().apply {
            set(HttpHeaderNames.CONTENT_TYPE, "text/event-stream; charset=UTF-8")
            set(HttpHeaderNames.CACHE_CONTROL, "no-cache")
            set(HttpHeaderNames.CONNECTION, "keep-alive")
            set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
            set(HttpHeaderNames.TRANSFER_ENCODING, "chunked")
        }
        ctx.write(response)
        ctx.writeAndFlush(Unpooled.copiedBuffer(": connected\n\n", Charsets.UTF_8))
        SseRegistry.add(ctx)
    }

    private fun handleAction(ctx: ChannelHandlerContext, uri: String, msg: FullHttpRequest) {
        val minecraft = MinecraftClient.getInstance()
        val screen = minecraft.currentScreen as? ClientScreen ?: Hud.instance ?: return

        screen.api(uri.removePrefix("/api/"), ctx, msg).send()
    }
}

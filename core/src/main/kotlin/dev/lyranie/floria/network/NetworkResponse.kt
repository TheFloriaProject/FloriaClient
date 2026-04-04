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

import com.google.gson.Gson
import dev.lyranie.floria.Floria
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpHeaderNames
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpVersion

class NetworkResponse(
    val ctx: ChannelHandlerContext,
    val status: HttpResponseStatus,
    val data: Any? = null,
    val serialize: Boolean = true,
    val gson: Gson = Floria.gson
) {
    companion object {
        fun empty(ctx: ChannelHandlerContext) = NetworkResponse(ctx, HttpResponseStatus.OK)
    }

    fun send() {
        val body = if (serialize) {
            gson.toJson(data).toByteArray(Charsets.UTF_8)
        } else {
            data.toString().toByteArray(Charsets.UTF_8)
        }
        val response = DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            Unpooled.wrappedBuffer(body)
        )
        response.headers()
            .set(HttpHeaderNames.CONTENT_TYPE, "application/json")
            .setInt(HttpHeaderNames.CONTENT_LENGTH, body.size)
            .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
            .set(HttpHeaderNames.CACHE_CONTROL, "no-cache")

        ctx.writeAndFlush(response)
            .addListener(ChannelFutureListener.CLOSE)
    }
}

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

package dev.lyranie.floria.render.screen

import com.google.gson.JsonObject
import dev.lyranie.floria.Floria
import dev.lyranie.floria.module.ModuleManager
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.render.ClientScreen
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus

class ClientHudScreen : ClientScreen("hud", true) {
    override fun api(path: String, ctx: ChannelHandlerContext, msg: FullHttpRequest) = when (path) {
        "modules" -> getModules(ctx)
        else -> NetworkResponse(ctx, HttpResponseStatus.OK)
    }

    private fun getModules(ctx: ChannelHandlerContext) = NetworkResponse(
        ctx,
        HttpResponseStatus.OK,
        ModuleManager
            .modules.map { module ->
                JsonObject().apply {
                    addProperty("id", module.id)
                    addProperty("name", module.name)
                    addProperty("description", module.description)
                    addProperty("toggled", module.toggled)
                    addProperty("keybind", module.keybind)
                    add("settings", Floria.gson.toJsonTree(module.settings))
                }
            }
    )
}

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
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.category.CategoryManager
import dev.lyranie.floria.module.ModuleManager
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.render.ClientScreen
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.util.CharsetUtil
import net.minecraft.client.input.KeyInput
import org.lwjgl.glfw.GLFW

class ClientClickGuiScreen : ClientScreen("click", true) {
    override fun api(path: String, ctx: ChannelHandlerContext, msg: FullHttpRequest) = when (path) {
        "categories" -> getCategories(ctx)
        "toggle" -> toggle(ctx, msg)
        "modules" -> getModules(ctx)
        "setting" -> updateSetting(ctx, msg)
        "collapse" -> collapse(ctx, msg)
        "position" -> updatePosition(ctx, msg)
        else -> NetworkResponse.empty(ctx)
    }

    private fun getCategories(ctx: ChannelHandlerContext): NetworkResponse {
        val body = ModuleManager
            .modules
            .groupBy { it.category }
            .map { (category, modules) ->
                val json = JsonObject()

                json.add("category", Floria.gson.toJsonTree(category))
                json.addProperty("collapsed", category.collapsed)
                json.add(
                    "modules",
                    Floria.gson.toJsonTree(
                        modules.map { module ->
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
                )

                json
            }

        return NetworkResponse(ctx, HttpResponseStatus.OK, body)
    }

    private fun toggle(ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val module = Floria.gson.fromJson(content, JsonObject::class.java)

        ModuleManager.modules.firstOrNull { it.id == module.get("id").asString }?.let {
            ModuleManager.toggleModule(it)
        }

        return NetworkResponse.empty(ctx)
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

    private fun updateSetting(ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        val empty = NetworkResponse.empty(ctx)
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val moduleId = data["module"].asString!!
        val settingData = data["setting"].asJsonObject!!
        val settingId = settingData["id"].asString!!
        val module = ModuleManager.modules.firstOrNull { it.id == moduleId }!!
        val setting = module.settings.firstOrNull { it.id == settingId }!!

        setting.fromJson(settingData)

        ModuleManager.save()

        return empty
    }

    private fun collapse(ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val categoryId = data["category"].asString!!
        val collapsed = data["collapsed"].asBoolean

        CategoryManager.categories.firstOrNull { it.id == categoryId }?.let {
            it.collapsed = collapsed
        }

        CategoryManager.save()

        return NetworkResponse.empty(ctx)
    }

    private fun updatePosition(ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val category = Floria.gson.fromJson(content, ClientCategory::class.java)

        CategoryManager.categories.firstOrNull { it.id == category.id }?.let {
            it.x = category.x
            it.y = category.y
        }

        CategoryManager.save()

        return NetworkResponse.empty(ctx)
    }

    override fun shouldPause() = false

    override fun keyPressed(input: KeyInput): Boolean {
        if (input.keycode == GLFW.GLFW_KEY_ESCAPE) {
            close()
        }

        return super.keyPressed(input)
    }

    override fun close() {
        client.execute {
            client.setScreen(null)
            ModuleManager.save()
        }
    }
}

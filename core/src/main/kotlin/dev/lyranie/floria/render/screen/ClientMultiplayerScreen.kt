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
import dev.lyranie.floria.account.AccountManager
import dev.lyranie.floria.mixin.accessor.ServerListAccessor
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.network.SseRegistry
import dev.lyranie.floria.render.ClientScreen
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.util.CharsetUtil
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen
import net.minecraft.client.network.CookieStorage
import net.minecraft.client.network.MultiplayerServerListPinger
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo
import net.minecraft.client.option.ServerList
import net.minecraft.network.NetworkingBackend
import net.minecraft.util.Util
import java.net.URI
import java.net.UnknownHostException
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ClientMultiplayerScreen : ClientScreen("multiplayer") {
    private var pinger: MultiplayerServerListPinger? = null
    private var serverList: ServerList? = null

    override fun api(path: String, ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        when (path) {
            "account" -> return AccountManager.getAccountData(ctx)
            "back" -> client.execute { client.setScreen(TitleScreen()) }
            "alt" -> AccountManager.newAccount()
            "servers" -> return getServers(ctx)
            "join" -> joinServer(msg)
            "delete" -> deleteServer(msg)
            "create" -> createServer(msg)
            "update" -> updateServer(msg)
            "connect" -> connectServer(msg)
            "discord" -> Util.getOperatingSystem().open(URI.create(Floria.Link.DISCORD))
            "github" -> Util.getOperatingSystem().open(URI.create(Floria.Link.GITHUB))
        }

        return NetworkResponse.empty(ctx)
    }

    private fun getServers(ctx: ChannelHandlerContext): NetworkResponse {
        val existing = serverList
        if (existing != null && existing is ServerListAccessor) {
            return NetworkResponse(ctx, HttpResponseStatus.OK, existing.getServers(), gson = Floria.globalGson)
        }

        serverList = null
        val list = ServerList(client)
        list.loadFile()
        serverList = list

        if (list !is ServerListAccessor) return NetworkResponse.empty(ctx)

        val servers = list.getServers()
        if (servers.isEmpty()) return NetworkResponse(ctx, HttpResponseStatus.OK, servers, gson = Floria.globalGson)

        println(servers)

        val activePinger = MultiplayerServerListPinger()
        pinger = activePinger

        val pending = AtomicInteger(servers.size)
        val threadPool = Executors.newFixedThreadPool(servers.size.coerceAtMost(5) + 1)

        for (server in servers) {
            threadPool.submit {
                try {
                    activePinger.add(
                        server,
                        { client.execute { list.saveFile() } },
                        {
                            threadPool.shutdown()
                            pinger = null
                            SseRegistry.push("servers:refetch")
                        },
                        NetworkingBackend.remote(client.options.shouldUseNativeTransport())
                    )
                } catch (_: UnknownHostException) {
                    threadPool.shutdown()
                    pinger = null
                    SseRegistry.push("servers:refetch")
                }
            }
        }

        threadPool.submit {
            val timeout = System.currentTimeMillis() + 10_000
            while (pending.get() > 0 && System.currentTimeMillis() < timeout) {
                Thread.sleep(50)
                activePinger.tick()
            }
            if (pending.get() > 0) {
                pinger = null
                SseRegistry.push("servers:refetch")
            }
        }

        return NetworkResponse(ctx, HttpResponseStatus.OK, servers, gson = Floria.globalGson)
    }

    fun joinServer(msg: FullHttpRequest) {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val serverInfo = ServerInfo(
            data["name"].asString,
            data["address"].asString,
            ServerInfo.ServerType.valueOf(data["serverType"].asString)
        )

        client.execute {
            ConnectScreen.connect(
                this,
                this.client,
                ServerAddress.parse(serverInfo.address),
                serverInfo,
                false,
                null as CookieStorage?
            )
        }
    }

    fun deleteServer(msg: FullHttpRequest) {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val targetAddress = data["address"].asString
        val targetName = data["name"].asString

        serverList?.let { serverList ->
            if (serverList !is ServerListAccessor) return

            val match = serverList.getServers().find { it.address == targetAddress && it.name == targetName }
            if (match != null) {
                serverList.remove(match)
                serverList.saveFile()
                SseRegistry.push("servers:refetch")
            }
        }
    }

    fun createServer(msg: FullHttpRequest) {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val targetAddress = data["address"].asString
        val targetName = data["name"].asString
        val serverInfo = ServerInfo(targetName, targetAddress, ServerInfo.ServerType.OTHER)

        serverList?.let { serverList ->
            if (serverList !is ServerListAccessor) return

            val match = serverList.getServers().find { it.address == targetAddress && it.name == targetName }
            if (match != null) {
                // todo: create notification: server exists
            } else {
                serverList.add(serverInfo, false)
                serverList.saveFile()
                this.serverList = null
                SseRegistry.push("servers:refetch")
            }
        }
    }

    fun updateServer(msg: FullHttpRequest) {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val old = data["old"].asJsonObject
        val targetAddress = old["address"].asString
        val targetName = old["name"].asString

        serverList?.let { serverList ->
            if (serverList !is ServerListAccessor) return

            val match = serverList.getServers().find { it.address == targetAddress && it.name == targetName }
            if (match != null) {
                match.name = data["name"].asString
                match.address = data["address"].asString
                serverList.saveFile()
                this.serverList = null
                SseRegistry.push("servers:refetch")
            }
        }
    }

    fun connectServer(msg: FullHttpRequest) {
        val content = msg.content().toString(CharsetUtil.UTF_8)
        val data = Floria.gson.fromJson(content, JsonObject::class.java)
        val serverInfo = ServerInfo("", data["address"].asString, ServerInfo.ServerType.OTHER)

        client.execute {
            ConnectScreen.connect(
                this,
                this.client,
                ServerAddress.parse(serverInfo.address),
                serverInfo,
                false,
                null as CookieStorage?
            )
        }
    }
}

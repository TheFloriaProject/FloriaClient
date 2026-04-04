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

import dev.lyranie.floria.Floria
import dev.lyranie.floria.account.AccountManager
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.render.ClientScreen
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen
import net.minecraft.client.gui.screen.option.OptionsScreen
import net.minecraft.client.gui.screen.world.SelectWorldScreen
import net.minecraft.util.Util
import java.net.URI

class ClientTitleScreen : ClientScreen("title") {
    override fun api(path: String, ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse {
        when (path) {
            "account" -> return AccountManager.getAccountData(ctx)
            "singleplayer" -> client.execute { client.setScreen(SelectWorldScreen(null)) }
            "multiplayer" -> client.execute { client.setScreen(MultiplayerScreen(null)) }
            "options" -> client.execute { client.setScreen(OptionsScreen(null, client.options)) }
            "quit" -> Floria.stop(client)
            "alt" -> AccountManager.newAccount()
            "discord" -> Util.getOperatingSystem().open(URI.create(Floria.Link.DISCORD))
            "github" -> Util.getOperatingSystem().open(URI.create(Floria.Link.GITHUB))
        }

        return NetworkResponse.empty(ctx)
    }
}

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

package dev.lyranie.floria.account

import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.lyranie.floria.Floria
import dev.lyranie.floria.mixin.accessor.MinecraftClientAccessor
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.util.Crypto
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.HttpResponseStatus
import net.minecraft.client.MinecraftClient
import net.minecraft.client.session.Session
import java.util.Optional
import java.util.UUID
import kotlin.io.path.notExists
import kotlin.io.path.readText

// Needs to be implemented properly with altening support
object AccountManager {
    private val gson = Gson()
    private val SAVE_FILE = Floria.SAVE_DIR.resolve("session.json")
    private var currentAccount: Session? = null

    fun save() {
        val session = getCurrentAccount()
        val (encryptedToken, iv) = Crypto.encrypt(session.accessToken)
        val uuid =
            session.uuidOrNull?.toString() ?: UUID.nameUUIDFromBytes("OfflinePlayer:${session.username}".toByteArray())
                .toString()

        SAVE_FILE.toFile().writeText(gson.toJson(PersistedSession(session.username, uuid, encryptedToken, iv)))
    }

    fun load(): Boolean {
        if (SAVE_FILE.notExists()) return false

        return runCatching {
            val persisted = gson.fromJson(SAVE_FILE.readText(), PersistedSession::class.java)
            val token = Crypto.decrypt(persisted.token, persisted.iv)
            val session = Session(
                persisted.username,
                UUID.fromString(persisted.uuid),
                token,
                Optional.empty(),
                Optional.empty()
            )
            val client = MinecraftClient.getInstance() ?: return false

            client as MinecraftClientAccessor
            client.setSession(session)

            currentAccount = session

            true
        }.getOrElse { e ->
            e.printStackTrace()
            false
        }
    }

    fun getCurrentAccount() = currentAccount ?: MinecraftClient.getInstance().session

    fun newAccount() {
        val client = MinecraftClient.getInstance() ?: return
        val username = (1..8).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")
        val uuid = UUID.nameUUIDFromBytes("OfflinePlayer:$username".toByteArray())
        val session = Session(username, uuid, "0", Optional.empty(), Optional.empty())

        client as MinecraftClientAccessor
        client.setSession(session)

        currentAccount = session
        save()
    }

    fun getAccountData(ctx: ChannelHandlerContext): NetworkResponse {
        val session = getCurrentAccount()
        val json = JsonObject().apply {
            addProperty("username", session.username)
            addProperty("uuid", session.uuidOrNull.toString())
            addProperty("avatar", "https://minotar.net/avatar/${session.username}/64")
        }

        return NetworkResponse(ctx, HttpResponseStatus.OK, json)
    }
}

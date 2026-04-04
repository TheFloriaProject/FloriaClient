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

package dev.lyranie.floria.util

import dev.lyranie.floria.Floria
import java.net.NetworkInterface
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"

    fun deriveMachineKey(): SecretKeySpec {
        val mac = runCatching {
            NetworkInterface.getNetworkInterfaces()
                .asSequence()
                .filter { !it.isLoopback && it.hardwareAddress != null }
                .firstNotNullOf { it.hardwareAddress }
                .joinToString(":") { "%02X".format(it) }
        }.getOrDefault("00:00:00:00:00:00")
        val osUser = System.getProperty("user.name") ?: "unknown"
        val raw = "$mac|$osUser|${Floria.MOD_ID}-account"
        val digest = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())

        return SecretKeySpec(digest, "AES")
    }

    fun encrypt(plaintext: String): Pair<String, String> {
        val cipher = Cipher.getInstance(ALGORITHM)

        cipher.init(Cipher.ENCRYPT_MODE, deriveMachineKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(plaintext.toByteArray())

        return Base64.getEncoder().encodeToString(encrypted) to Base64.getEncoder().encodeToString(iv)
    }

    fun decrypt(cipherTextBase64: String, ivBase64: String): String {
        val cipher = Cipher.getInstance(ALGORITHM)
        val iv = IvParameterSpec(Base64.getDecoder().decode(ivBase64))

        cipher.init(Cipher.DECRYPT_MODE, deriveMachineKey(), iv)

        val decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherTextBase64))

        return String(decrypted)
    }
}

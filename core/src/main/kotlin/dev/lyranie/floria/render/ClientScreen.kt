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

package dev.lyranie.floria.render

import com.cinemamod.mcef.MCEF
import com.cinemamod.mcef.MCEFBrowser
import dev.lyranie.floria.network.GuiServer
import dev.lyranie.floria.network.NetworkResponse
import dev.lyranie.floria.util.ChatUtils
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.FullHttpRequest
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper
import org.joml.Vector2i

abstract class ClientScreen(
    private val id: String,
    private val transparent: Boolean = false,
) : Screen(
    Text.literal("")
) {
    protected var browser: MCEFBrowser? = null
    protected var lastWidth = -1
    protected var lastHeight = -1

    private var startFrames = 20

    abstract fun api(path: String, ctx: ChannelHandlerContext, msg: FullHttpRequest): NetworkResponse

    protected fun scale() = Vector2i(client.window.framebufferWidth, client.window.framebufferHeight)
    private fun scale(value: Double) = (value * client.window.scaleFactor).toInt()

    override fun init() {
        val scale = scale()

        if (browser == null) {
            browser = MCEF.createBrowser("http://localhost:${GuiServer.port}/$id", transparent)
        }

        val browser = browser ?: return

        browser.resize(scale.x, scale.y)
        lastWidth = scale.x
        lastHeight = scale.y

        client.execute {
            browser.resize(scale.x + 1, scale.y)
            browser.resize(scale.x, scale.y)
        }
    }

    override fun renderBackground(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        val color = if (transparent) {
            0x00000000
        } else {
            ChatUtils.Color.BG
        }

        context.fill(0, 0, width, height, color)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        val browser = browser ?: return
        val scale = scale()

        if (scale.x != lastWidth || scale.y != lastHeight) {
            browser.resize(scale.x, scale.y)
            lastWidth = scale.x
            lastHeight = scale.y
        }

        val textureId = browser.textureIdentifier ?: return
        val alpha = if (startFrames != 0) {
            startFrames--
            0f
        } else {
            1f
        }

        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            textureId,
            0, 0,
            0f, 0f,
            width, height,
            width, height,
            ColorHelper.getWhite(alpha)
        )
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        val browser = browser ?: return

        browser.sendMouseMove(scale(mouseX), scale(mouseY))
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        val browser = browser ?: return false

        browser.sendMousePress(scale(click.x), scale(click.y), click.button())
        return true
    }

    override fun mouseReleased(click: Click): Boolean {
        val browser = browser ?: return false

        browser.sendMouseRelease(scale(click.x), scale(click.y), click.button())
        return true
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        verticalAmount: Double,
    ): Boolean {
        val browser = browser ?: return false

        browser.sendMouseWheel(scale(mouseX), scale(mouseY), verticalAmount, 0)
        return true
    }

    override fun keyPressed(input: KeyInput): Boolean {
        val browser = browser ?: return false

        browser.sendKeyPress(input.keycode, input.scancode.toLong(), input.modifiers)
        return true
    }

    override fun keyReleased(input: KeyInput): Boolean {
        browser?.sendKeyRelease(input.keycode, input.scancode.toLong(), input.modifiers)
        return true
    }

    override fun charTyped(input: CharInput?): Boolean {
        browser?.sendKeyTyped(input?.codepoint?.toChar()!!, input.modifiers)
        return true
    }

    override fun shouldCloseOnEsc() = false

    override fun removed() {
        browser?.close()
        browser = null
    }

    override fun shouldPause() = false
}

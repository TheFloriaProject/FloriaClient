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

package dev.lyranie.floria.mixin

import dev.lyranie.floria.Floria
import dev.lyranie.floria.util.ChatUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.SplashOverlay
import net.minecraft.resource.ResourceReload
import net.minecraft.util.Identifier
import net.minecraft.util.Util
import net.minecraft.util.math.MathHelper
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(SplashOverlay::class)
class SplashOverlayMixin {
    private val logoId = Identifier.of(Floria.MOD_ID, "textures/gui/logo.png")

    @Shadow
    private var reloading: Boolean = false

    @Shadow
    private var reloadStartTime: Long = -1L

    @Shadow
    private var reloadCompleteTime: Long = -1L

    @Shadow
    lateinit var client: MinecraftClient

    @Shadow
    private var progress: Float = 0f

    @Shadow
    lateinit var reload: ResourceReload

    @Shadow
    private fun renderProgressBar(
        @Suppress("UNUSED_PARAMETER") context: DrawContext,
        @Suppress("UNUSED_PARAMETER") minX: Int,
        @Suppress("UNUSED_PARAMETER") minY: Int,
        @Suppress("UNUSED_PARAMETER") maxX: Int,
        @Suppress("UNUSED_PARAMETER") maxY: Int,
        @Suppress("UNUSED_PARAMETER") opacity: Float,
    ) {
        // detekt:disable EmptyFunctionBlock
    }

    @Inject(method = ["render"], at = [At("HEAD")], cancellable = true)
    fun render(
        context: DrawContext,
        @Suppress("UNUSED_PARAMETER") mouseX: Int,
        @Suppress("UNUSED_PARAMETER") mouseY: Int,
        @Suppress("UNUSED_PARAMETER") deltaTicks: Float,
        callbackInfo: CallbackInfo,
    ) {
        val width = context.scaledWindowWidth
        val height = context.scaledWindowHeight
        val now = Util.getMeasuringTimeMs()

        if (reloading && reloadStartTime == -1L) {
            reloadStartTime = now
        }

        val delta = if (reloadCompleteTime > -1L) (now - reloadCompleteTime) / 1000f else -1f

        if (delta >= 1f) {
            context.fill(0, 0, width, height, ChatUtils.Color.BG)
            client.overlay = null
            callbackInfo.cancel()

            return
        }

        context.fill(0, 0, width, height, ChatUtils.Color.BG)

        val logoWidth = (width * 0.35f).toInt()
        val logoHeight = (logoWidth * 453f / 1280f).toInt()
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            logoId,
            width / 2 - logoWidth / 2,
            height / 2 - logoHeight / 2,
            0f,
            0f,
            logoWidth,
            logoHeight,
            logoWidth,
            logoHeight
        )

        if (delta < 0f) {
            val barW = logoWidth
            val barX = width / 2 - barW / 2
            val barY = (height * 0.8325f).toInt()

            val r = reload.progress
            progress = MathHelper.clamp(progress * 0.95f + r * 0.05f, 0f, 1f)

            renderProgressBar(context, barX, barY - 5, barX + barW, barY + 5, 1f)
        }

        if (delta >= 0f) {
            val alpha = MathHelper.clamp(delta, 0f, 1f)
            val alphaInt = (alpha * 255).toInt() shl 24
            context.fill(0, 0, width, height, alphaInt or 0x0038c5d8)
        }

        callbackInfo.cancel()
    }
}

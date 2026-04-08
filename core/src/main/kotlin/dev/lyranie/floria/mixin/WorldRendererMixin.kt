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

import com.mojang.blaze3d.buffers.GpuBufferSlice
import dev.lyranie.floria.event.EventHandler
import dev.lyranie.floria.event.events.WorldRenderEvent
import net.minecraft.client.render.BufferBuilderStorage
import net.minecraft.client.render.Camera
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.ObjectAllocator
import net.minecraft.client.util.math.MatrixStack
import org.joml.Matrix4f
import org.joml.Vector4f
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(WorldRenderer::class)
class WorldRendererMixin {
    @Shadow
    private lateinit var bufferBuilders: BufferBuilderStorage

    @Inject(method = ["render"], at = [At("TAIL")])
    fun render(
        @Suppress("UNUSED_PARAMETER") allocator: ObjectAllocator,
        @Suppress("UNUSED_PARAMETER") tickCounter: RenderTickCounter,
        @Suppress("UNUSED_PARAMETER") renderBlockOutline: Boolean,
        camera: Camera,
        positionMatrix: Matrix4f,
        @Suppress("UNUSED_PARAMETER") basicProjectionMatrix: Matrix4f,
        @Suppress("UNUSED_PARAMETER") projectionMatrix: Matrix4f,
        @Suppress("UNUSED_PARAMETER") fogBuffer: GpuBufferSlice,
        @Suppress("UNUSED_PARAMETER") fogColor: Vector4f,
        @Suppress("UNUSED_PARAMETER") renderSky: Boolean,
        callbackInfo: CallbackInfo,
    ) {
        val matrices = MatrixStack()
        matrices.multiplyPositionMatrix(positionMatrix)

        EventHandler.handleEvent(WorldRenderEvent(matrices, bufferBuilders.entityVertexConsumers, camera, callbackInfo))
    }
}

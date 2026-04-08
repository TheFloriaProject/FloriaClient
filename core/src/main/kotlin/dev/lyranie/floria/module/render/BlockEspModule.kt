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

package dev.lyranie.floria.module.render

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientMode
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.api.setting.delegate.color
import dev.lyranie.floria.api.setting.delegate.enum
import dev.lyranie.floria.api.setting.delegate.list
import dev.lyranie.floria.api.setting.delegate.range
import dev.lyranie.floria.module.render.blockEsp.BlockEsp3DMode
import net.minecraft.item.BlockItem
import net.minecraft.registry.Registries
import net.minecraft.util.math.BlockPos

class BlockEspModule : ClientModule("blockEsp", ClientCategory.RENDER) {
    private val typeSetting by enum<Type>("type", "Type") {
        modes.forEach { it.value.onDisable() }
        mode.onEnable()
    }
    private val radiusSetting by range("radius", "Radius", 1.0, 64.0, 1.0, 16.0)
    private val lineWidthSetting by range("lineWidth", "Line width", 1.0, 100.0, 1.0, 1.0)
    private val lineColorSetting by color("lineColor", "Line color")
    private val blocksSetting by list<String>( // todo: implement setting renderer
        "blocks",
        "Blocks",
        Registries.ITEM.filterIsInstance<BlockItem>().map { Registries.ITEM.getId(it).toString() }.toMutableList()
    )

    private val mode: ClientMode get() = modes[typeSetting.selected]!!
    private val modes = mapOf(
        Type.RENDER_3D to BlockEsp3DMode(
            this,
            { radiusSetting.value.toInt() },
            { lineWidthSetting.value.toFloat() },
            { lineColorSetting.color }
        )
    )

    private enum class Type {
        RENDER_3D,
//        RENDER_2D
    }

    override fun onEnable() = mode.onEnable()
    override fun onDisable() = modes.forEach { it.value.onDisable() }
    override fun onEvent(event: ClientEvent) = mode.onEvent(event)

    fun getBlocks(radius: Int): List<BlockPos> {
        val player = client?.player ?: return listOf()
        val world = player.entityWorld
        val playerChunkX = player.chunkPos.x
        val playerChunkZ = player.chunkPos.z

        val blocks = arrayListOf<BlockPos>()

        for (x in -radius..radius) {
            for (z in -radius..radius) {
                val chunk = world.getChunk(playerChunkX + x, playerChunkZ + z) ?: continue

                for (blockEntity in chunk.blockEntities.values) {
                    blocks.add(blockEntity.pos)
                }
            }
        }

        return blocks
    }
}

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

package dev.lyranie.floria.module.movement

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.event.events.PlayerTickEvent
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.util.math.Box
import java.util.stream.Stream
import java.util.stream.StreamSupport

class StepModule : ClientModule("step", ClientCategory.MOVEMENT) {
    private val steps = listOf(
        0.41999,
        0.75319,
        1.24918
    )

    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return
        if (client == null) return

        val player = event.player

        if (!player.isOnGround) return
        if (!player.horizontalCollision) return
        if (player.isJumping) return
        if (player.isInLava) return
        if (player.isTouchingWater) return

        val x = player.x
        val z = player.z
        val box = player.boundingBox.offset(0.0, 0.05, 0.0).expand(0.05)

        if (client!!.world!!.canCollide(player, box.offset(0.0, 1.0, 0.0))) return

        var stepHeight = getCollisions(box).mapToDouble { it.maxY }.max().orElse(Double.NEGATIVE_INFINITY)
        stepHeight -= player.y.toFloat()

        if (stepHeight !in 0.0..1.0) return

        steps.forEach { step ->
            val y = player.y + step * stepHeight

            player.networkHandler.sendPacket(
                PlayerMoveC2SPacket.PositionAndOnGround(
                    x,
                    y,
                    z,
                    false,
                    player.horizontalCollision
                )
            )
        }
        player.setPos(x, player.y + stepHeight, z)
    }

    /*
    Taken from wurst client
    https://github.com/Wurst-Imperium/Wurst7/blob/master/src/main/java/net/wurstclient/util/BlockUtils.java#L179
     */

    private fun getCollisions(box: Box): Stream<Box> {
        val blockCollisions = client!!.world!!.getBlockCollisions(client!!.player, box)

        return StreamSupport.stream(blockCollisions.spliterator(), false).flatMap { it.boundingBoxes.stream() }
            .filter { it.intersects(box) }
    }

    /*
    public static Stream<AABB> getBlockCollisions(AABB box)
	{
		Iterable<VoxelShape> blockCollisions =
			MC.level.getBlockCollisions(MC.player, box);

		return StreamSupport.stream(blockCollisions.spliterator(), false)
			.flatMap(shape -> shape.toAabbs().stream())
			.filter(shapeBox -> shapeBox.intersects(box));
	}
     */
}

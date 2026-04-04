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

package dev.lyranie.floria.module.combat

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.event.events.PlayerTickEvent
import net.minecraft.component.DataComponentTypes
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.item.ItemStack
import net.minecraft.registry.RegistryKeys
import net.minecraft.screen.slot.SlotActionType

class AutoArmorModule : ClientModule("autoArmor", ClientCategory.COMBAT) {
    override fun onEvent(event: ClientEvent) {
        if (event !is PlayerTickEvent) return

        client?.player?.let {
            equipArmor(it.inventory)
        }
    }

    private fun equipArmor(inventory: PlayerInventory) {
        val player = client?.player ?: return
        val handler = player.playerScreenHandler
        val interactionManager = client?.interactionManager ?: return

        getBestArmorPieces(inventory).forEach { (slot, stack) ->
            val armorScreenSlot = armorScreenSlots[slot] ?: return@forEach
            val currentItem = player.getEquippedStack(slot)
            if (!currentItem.isEmpty && getDefense(stack) <= getDefense(currentItem)) return@forEach

            val stackIndex = inventory.indexOf(stack)
            if (stackIndex == -1) return@forEach

            val screenSlotIndex = rawToScreenSlot(stackIndex)

            interactionManager.clickSlot(handler.syncId, screenSlotIndex, 0, SlotActionType.PICKUP, player)
            interactionManager.clickSlot(handler.syncId, armorScreenSlot, 0, SlotActionType.PICKUP, player)
            interactionManager.clickSlot(handler.syncId, screenSlotIndex, 0, SlotActionType.PICKUP, player)
        }
    }

    private fun rawToScreenSlot(rawIndex: Int) = if (rawIndex < 9) {
        rawIndex + 36
    } else {
        rawIndex
    }

    private val armorScreenSlots = mapOf(
        EquipmentSlot.HEAD to 5,
        EquipmentSlot.CHEST to 6,
        EquipmentSlot.LEGS to 7,
        EquipmentSlot.FEET to 8
    )

    private fun getDefense(stack: ItemStack): Int {
        val armor = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS)?.modifiers()
            ?.filter { it.attribute() == EntityAttributes.ARMOR }
            ?.sumOf { it.modifier().value.toInt() }
            ?: 0
        val registryManager = client?.world?.registryManager ?: return armor
        val protection = stack.get(DataComponentTypes.ENCHANTMENTS)
            ?.getLevel(
                registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.PROTECTION)
            ) ?: 0

        return armor + protection
    }

    private fun getBestArmorPieces(inventory: PlayerInventory): Map<EquipmentSlot, ItemStack> {
        val armorSlots = setOf(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)

        return (0 until inventory.size())
            .map { inventory.getStack(it) }
            .filter { stack ->
                val slot = stack.takeUnless { it.isEmpty }
                    ?.item?.components?.get(DataComponentTypes.EQUIPPABLE)?.slot
                slot in armorSlots
            }
            .groupBy { it.item.components.get(DataComponentTypes.EQUIPPABLE)!!.slot }
            .mapValues { (_, stacks) -> stacks.maxBy { getDefense(it) } }
    }
}

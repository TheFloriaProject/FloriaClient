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

package dev.lyranie.floria.module.misc

import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientCategory
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.event.events.ItemTooltipEvent
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Colors
import java.nio.charset.StandardCharsets

class DataSizeModule : ClientModule("dataSize", ClientCategory.MISC) {
    override fun onEvent(event: ClientEvent) {
        if (event !is ItemTooltipEvent) return

        val stack = event.stack
        val hasBook = stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT) != null
        val hasBundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS) != null
        val hasContainer = stack.get(DataComponentTypes.CONTAINER) != null

        if (!hasBook && !hasBundle && !hasContainer) return

        val bytes = calculateSize(stack)
        val sizeLabel = Text.literal(format(bytes)).withColor(Colors.GRAY)

        event.textConsumer.accept(sizeLabel)
    }

    private fun calculateSize(stack: ItemStack): Long {
        if (stack.isEmpty) return 0L
        var total = 0L

        stack.get(DataComponentTypes.WRITTEN_BOOK_CONTENT)?.let { book ->
            total += book.title.raw.toByteArray(StandardCharsets.UTF_8).size
            total += book.author.toByteArray(StandardCharsets.UTF_8).size
            total += book.pages().sumOf { page ->
                page.raw.string.toByteArray(StandardCharsets.UTF_8).size.toLong()
            }
        }

        stack.get(DataComponentTypes.CONTAINER)?.iterateNonEmptyCopy()?.forEach { inner ->
            total += calculateSize(inner)
        }

        stack.get(DataComponentTypes.BUNDLE_CONTENTS)?.iterate()?.forEach { inner ->
            total += calculateSize(inner)
        }

        return total
    }

    private fun format(bytes: Long): String = when {
        bytes < 1_024L -> "$bytes B"
        bytes < 1_048_576L -> "%.2f KB".format(bytes / 1_024.0)
        bytes < 1_073_741_824L -> "%.2f MB".format(bytes / 1_048_576.0)
        else -> "%.2f GB".format(bytes / 1_073_741_824.0)
    }
}

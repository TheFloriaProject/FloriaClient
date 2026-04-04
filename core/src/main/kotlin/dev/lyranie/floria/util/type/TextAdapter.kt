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

package dev.lyranie.floria.util.type

import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import net.minecraft.text.PlainTextContent
import net.minecraft.text.Style
import net.minecraft.text.StyleSpriteSource
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.text.TranslatableTextContent
import net.minecraft.util.Identifier
import java.lang.reflect.Type

class TextAdapter : JsonSerializer<Text>, JsonDeserializer<Text> {

    override fun serialize(src: Text, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val obj = JsonObject()

        when (val contents = src.content) {
            is PlainTextContent -> {
                obj.addProperty("type", "text")
                obj.addProperty("text", contents.string())
            }

            is TranslatableTextContent -> {
                obj.addProperty("type", "translatable")
                obj.addProperty("text", contents.key)
            }

            else -> {
                obj.addProperty("type", "text")
                obj.addProperty("text", src.string)
            }
        }

        val style = src.style

        style.color?.let { obj.addProperty("color", it.name) }
        if (style.isBold) obj.addProperty("bold", true)
        if (style.isItalic) obj.addProperty("italic", true)
        if (style.isUnderlined) obj.addProperty("underlined", true)
        if (style.isStrikethrough) obj.addProperty("strikethrough", true)
        if (style.isObfuscated) obj.addProperty("obfuscated", true)
        if (style.font != StyleSpriteSource.DEFAULT) obj.addProperty("font", style.font.toString())

        val siblings = src.siblings
        if (siblings.isNotEmpty()) {
            val extra = JsonArray()
            siblings.forEach { sibling ->
                val collapsed = sibling.literalString
                if (collapsed != null) {
                    extra.add(collapsed)
                } else {
                    extra.add(serialize(sibling, typeOfSrc, context))
                }
            }
            obj.add("extra", extra)
        }

        return obj
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Text {
        if (json.isJsonPrimitive) return Text.literal(json.asString)

        val obj = json.asJsonObject
        val text = obj.get("text")?.asString ?: ""
        val component = Text.literal(text)

        var style = Style.EMPTY

        obj.get("color")?.asString?.let { raw ->
            TextColor.parse(raw).result().orElse(null)?.let { style = style.withColor(it) }
        }
        if (obj.get("bold")?.asBoolean == true) style = style.withBold(true)
        if (obj.get("italic")?.asBoolean == true) style = style.withItalic(true)
        if (obj.get("underlined")?.asBoolean == true) style = style.withUnderline(true)
        if (obj.get("strikethrough")?.asBoolean == true) style = style.withStrikethrough(true)
        if (obj.get("obfuscated")?.asBoolean == true) style = style.withObfuscated(true)
        obj.get("font")?.asString?.let {
            style = style.withFont(StyleSpriteSource.Font(Identifier.of(it)))
        }

        component.fillStyle(style)

        obj.getAsJsonArray("extra")?.forEach { element ->
            component.append(deserialize(element, typeOfT, context))
        }

        return component
    }
}

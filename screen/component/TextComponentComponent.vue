<!--
parts taken from LiquidBounce by CCBlueX
licensed under the GPL-3.0
https://www.gnu.org/licenses/gpl-3.0.en.html

taken from here
https://github.com/CCBlueX/LiquidBounce/blob/nextgen/src-theme/src/routes/menu/common/TextComponent.svelte
-->
<script setup lang="ts">
import {ref} from "vue"
import {TextComponent} from "../util/types"

defineProps<{
    textComponent: TextComponent | string,
    allowPreformatting?: boolean,
    preFormattingMonospace?: boolean,
    inheritedColor?: string,
    inheritedStrikethrough?: boolean,
    inheritedItalic?: boolean,
    inheritedUnderlined?: boolean,
    inheritedBold?: boolean,
    fontSize?: string
}>()

const colors = ref<{ [name: string]: string }>({
    black: "#000000",
    dark_blue: "#0000aa",
    dark_green: "#00aa00",
    dark_aqua: "#00aaaa",
    dark_red: "#aa0000",
    dark_purple: "#aa00aa",
    gold: "#ffaa00",
    gray: "#aaaaaa",
    dark_gray: "#555555",
    blue: "#5555ff",
    green: "#55ff55",
    aqua: "#55ffff",
    red: "#ff5555",
    light_purple: "#ff55ff",
    yellow: "#ffff55",
    white: "#ffffff"
})

function translateColor(color: string): string {
    if (!color) {
        return colors.value.white
    }
    if (color.startsWith("#")) {
        return color
    } else {
        return colors[color]
    }
}

function convertLegacyCodes(text: string) {
    let obfuscated = false
    let bold = false
    let strikethrough = false
    let underlined = false
    let italic = false
    let color = colors.value.black

    function reset() {
        obfuscated = false
        bold = false
        strikethrough = false
        underlined = false
        italic = false
        color = colors.value.black
    }

    const components: TextComponent[] = []
    const textParts = (text.startsWith("§") ? text : `§f${text}`).split("§")

    for (const p of textParts) {
        const code = p.charAt(0)
        const t = p.slice(1)

        switch (code) {
            case "k":
                obfuscated = true
                break
            case "l":
                bold = true
                break
            case "m":
                strikethrough = true
                break
            case "n":
                underlined = true
                break
            case "o":
                italic = true
                break
            case "r":
                reset()
                break
            default:
                color = colors[Object.keys(colors)[parseInt(code, 16)]] ?? colors.value.black
                break
        }

        components.push({
            color,
            bold,
            italic,
            underlined,
            obfuscated,
            strikethrough,
            text: t,
        })
    }

    return {
        extra: components
    } as TextComponent
}
</script>
<template>
    <TextComponentComponent v-if="typeof textComponent === 'string'"
                            :fontSize="fontSize"
                            :allowPreformatting="allowPreformatting"
                            :preFormattingMonospace="preFormattingMonospace"
                            :textComponent="convertLegacyCodes(textComponent as string)"
    />
    <span v-else>
        <span v-if="textComponent.text">
            <span v-if="!textComponent.text.includes('§')"
                  :class="{
                      [$style.text]: true,
                      [$style.bold]: textComponent.bold != undefined ? textComponent.bold : inheritedBold,
                      [$style.italic]: textComponent.italic !== undefined ? textComponent.italic : inheritedItalic,
                      [$style.underlined]: textComponent.underlined !== undefined ? textComponent.underlined : inheritedUnderlined,
                      [$style.strikethrough]: textComponent.strikethrough !== undefined ? textComponent.strikethrough : inheritedStrikethrough,
                      [$style.allow_preformatting]: allowPreformatting,
                      [$style.monospace]: preFormattingMonospace && allowPreformatting
                      }"
                  :style="{color: textComponent.color !== undefined ? translateColor(textComponent.color) : translateColor(inheritedColor), fontSize}">{{
                    textComponent.text
                }}</span>
            <TextComponentComponent v-else :allowPreformatting="allowPreformatting"
                                    :preFormattingMonospace="preFormattingMonospace" :fontSize="fontSize"
                                    :inheritedColor="textComponent.color !== undefined ? textComponent.color : inheritedColor"
                                    :inheritedBold="textComponent.bold !== undefined ? textComponent.bold : inheritedBold"
                                    :inheritedItalic="textComponent.italic !== undefined ? textComponent.italic : inheritedItalic"
                                    :inheritedUnderlined="textComponent.underlined !== undefined ? textComponent.underlined : inheritedUnderlined"
                                    :inheritedStrikethrough="textComponent.strikethrough !== undefined ? textComponent.strikethrough : inheritedStrikethrough"
                                    :textComponent="convertLegacyCodes(textComponent.text)"/>
        </span>
        <span v-if="textComponent.extra">
            <TextComponentComponent v-for="extra in textComponent.extra" :allowPreformatting="allowPreformatting"
                                    :preFormattingMonospace="preFormattingMonospace" :fontSize="fontSize"
                                    :inheritedColor="textComponent.color !== undefined ? textComponent.color : inheritedColor"
                                    :inheritedBold="textComponent.bold !== undefined ? textComponent.bold : inheritedBold"
                                    :inheritedItalic="textComponent.italic !== undefined ? textComponent.italic : inheritedItalic"
                                    :inheritedUnderlined="textComponent.underlined !== undefined ? textComponent.underlined : inheritedUnderlined"
                                    :inheritedStrikethrough="textComponent.strikethrough !== undefined ? textComponent.strikethrough : inheritedStrikethrough"
                                    :textComponent="extra"/>
        </span>
    </span>
</template>

<style module>
.text {
    display: inline;

    &.allow_preformatting {
        white-space: pre;
    }

    &.monospace {
        font-family: monospace;
    }

    &.bold {
        font-weight: 500;
    }

    &.italic {
        font-style: italic;
    }

    &.underlined {
        text-decoration: underline;
    }

    &.strikethrough {
        text-decoration: line-through;
    }
}
</style>

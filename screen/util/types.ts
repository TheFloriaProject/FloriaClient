/*
parts taken from LiquidBounce by CCBlueX
licensed under the GPL-3.0
https://www.gnu.org/licenses/gpl-3.0.en.html

code has been taken from this file and is explicitly
marked as such with `LB`
https://github.com/CCBlueX/LiquidBounce/blob/nextgen/src-theme/src/routes/menu/common/TextComponent.svelte
 */

export type Module = {
    id: string
    name: string
    description: string
    category: Category
    keybind: string
    toggled: boolean
    settings: Setting[]
}

export type Category = {
    id: string
    name: string
    x: number
    y: number
}

export type Setting = BooleanSetting | RangeSetting | EnumSetting | ColorSetting

export type BooleanSetting = {
    type: string
    id: string
    name: string
    enabled: boolean
}

export type RangeSetting = {
    type: string
    id: string
    name: string
    min: number
    max: number
    step: number
    value: number
}

export type EnumSetting = {
    type: string
    id: string
    name: string
    options: string[]
    index: number
}

export type ColorSetting = {
    type: string
    id: string
    name: string
    color: number
}

export type ServerInfo = {
    name: string
    address: string
    favicon: Uint8Array
    players: Players
    label: TextComponent
    serverType: ServerType
}

type Players = {
    max: number
    online: number
}

export enum ServerType {
    LAN,
    REALM,
    OTHER
}

export type ModuleGroup = {
    category: Category
    collapsed: boolean
    modules: Module[]
}

export type Notification = {
    type: 'INFO' | 'WARNING' | 'ERROR'
    title: string
    description?: string
}

// LB
export interface TextComponent {
    type?: string;
    extra?: (TextComponent | string)[];
    color: string;
    bold?: boolean;
    italic?: boolean;
    underlined?: boolean;
    strikethrough?: boolean;
    obfuscated?: boolean;
    font?: string;
    text: string;
}

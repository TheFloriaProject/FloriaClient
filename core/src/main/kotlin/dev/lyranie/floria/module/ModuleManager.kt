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

package dev.lyranie.floria.module

import dev.lyranie.floria.Floria
import dev.lyranie.floria.api.FloriaAddon
import dev.lyranie.floria.api.event.ClientEvent
import dev.lyranie.floria.api.module.ClientModule
import dev.lyranie.floria.module.combat.AutoArmorModule
import dev.lyranie.floria.module.combat.AutoLeaveModule
import dev.lyranie.floria.module.exploit.AutoRespawnModule
import dev.lyranie.floria.module.exploit.AutoclickerModule
import dev.lyranie.floria.module.exploit.ChestStealerModule
import dev.lyranie.floria.module.exploit.NoFallDamageModule
import dev.lyranie.floria.module.exploit.NoHungerModule
import dev.lyranie.floria.module.exploit.PortalGuiModule
import dev.lyranie.floria.module.misc.DataSizeModule
import dev.lyranie.floria.module.misc.PanicModule
import dev.lyranie.floria.module.movement.AirJumpModule
import dev.lyranie.floria.module.movement.AutoWalkModule
import dev.lyranie.floria.module.movement.BunnyHopModule
import dev.lyranie.floria.module.movement.DolphinModule
import dev.lyranie.floria.module.movement.FlyModule
import dev.lyranie.floria.module.movement.InventoryMoveModule
import dev.lyranie.floria.module.movement.SneakModule
import dev.lyranie.floria.module.movement.SpeedModule
import dev.lyranie.floria.module.movement.SprintModule
import dev.lyranie.floria.module.network.PacketDelayModule
import dev.lyranie.floria.module.network.PausePacketsModule
import dev.lyranie.floria.module.render.BlockEspModule
import dev.lyranie.floria.module.render.CustomGuiModule
import dev.lyranie.floria.module.render.FullBrightModule
import dev.lyranie.floria.network.SseRegistry
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer
import kotlin.io.path.notExists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object ModuleManager {
    private val SAVE_FILE = Floria.SAVE_DIR.resolve("modules.json")

    val modules = CopyOnWriteArrayList<ClientModule>()

    fun init() {
        registerBuiltins()

        if (SAVE_FILE.notExists()) save()
    }

    private fun registerBuiltins() {
        /* combat */
        modules.add(AutoArmorModule())
        modules.add(AutoLeaveModule())

        /* exploit */
        modules.add(AutoclickerModule())
        modules.add(AutoRespawnModule())
        modules.add(NoFallDamageModule())
        modules.add(NoHungerModule())
        modules.add(PortalGuiModule())
        modules.add(ChestStealerModule())

        /* misc */
        modules.add(PanicModule())
        modules.add(DataSizeModule())

        /* movement */
        modules.add(BunnyHopModule())
        modules.add(DolphinModule())
        modules.add(FlyModule())
        modules.add(InventoryMoveModule())
        modules.add(SneakModule())
        modules.add(SpeedModule())
        modules.add(SprintModule())
        modules.add(AirJumpModule())
        modules.add(AutoWalkModule())

        /* network */
        modules.add(PacketDelayModule())
        modules.add(PausePacketsModule())

        /* render */
        modules.add(FullBrightModule())
        modules.add(CustomGuiModule())
        modules.add(BlockEspModule())
    }

    fun registerAddon(addon: FloriaAddon) {
        val addonModules = arrayListOf<ClientModule>()

        addon.registerModules(addonModules)
        modules.addAll(addonModules)
    }

    fun save() {
        SAVE_FILE.writeText(Floria.gson.toJson(modules.toTypedArray()))
    }

    fun load() {
        val saved = SAVE_FILE.readText()
        val loaded = Floria.gson.fromJson(saved, Array<ClientModule>::class.java)

        loaded.forEach { module ->
            modules.firstOrNull { it.id == module.id }?.let { live ->
                live.toggled = module.toggled
                live.keybind = module.keybind

                module.settings.forEach { savedSetting ->
                    live.settings.firstOrNull { it.id == savedSetting.id }
                        ?.fromJson(Floria.gson.toJsonTree(savedSetting).asJsonObject)
                }
            }
        }
    }

    fun handleEvent(event: ClientEvent) {
        modules.forEach { module ->
            if (module.toggled) module.onEvent(event)
        }
    }

    fun handleKeyPress(key: Int) {
        modules.forEach(
            Consumer { module ->
                if (key == module.keybind) module.toggle()
            }
        )
    }

    fun toggleModule(module: ClientModule) {
        module.toggle()
        SseRegistry.push("modules:refetch")
        save()
    }

    fun disableModule(module: ClientModule) {
        module.toggled = false
        module.onDisable()
        SseRegistry.push("modules:refetch")
        save()
    }
}

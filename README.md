<p align="center">
<img src="./core/src/main/resources/floria.png" alt="logo" width="10%"/>
</p>

<h1 align="center">Floria Client</h1>
<p align="center">A hacked client mod for Minecraft</p>

## Modules

<details>
<summary>Combat</summary>

- AutoArmor
- AutoLeave
</details>

<details>
<summary>Exploit</summary>

- Autoclicker
- AutoRespawn
- ChestStealer
- ClientSpoof
- NoFallDamage
- NoHunger
- PortalGui
- FastBreak
</details>

<details>
<summary>Misc</summary>

- DataSize
- Panic
</details>

<details>
<summary>Movement</summary>

- AirJump
- AutoWalk
- BunnyHop
- Dolphin
- Fly (VANILLA, VELOCITY)
- InventoryMove
- SafeWalk
- Sneak (VANILLA, PACKET)
- Speed
- Spider
- Sprint
- Step
</details>

<details>
<summary>Network</summary>

- PacketDelay
- PacketLogger
- PausePackets
</details>

<details>
<summary>Render</summary>

- BlockEsp
- CustomGui
- FullBright
</details>

## Project setup

The project is split in two: The api, and the implementation. The api is a separate gradle module from the
implementation to keep it as small and compact as possible for plugin development. The implementation houses the core
logic and all modules and features of the client.

## Api

The Floria client provides a stable api which allows you to create your own modules, categories and commands. We also
provide a [plugin template](https://github.com/TheFloriaProject/FloriaAddonTemplate) to make understanding plugin
development as straight forward as possible.

## Contributing

Contributions to the project as a whole are always welcome and encouraged. If you run into any issues feel free to open
an issue, or create a pull request with the solution. The project is held to a strict coding standard and naming rules.
before pushing make sure to run the build task (which runs detekt and ktlint) to make sure the code is formatted. All
files **must** contain this projects copyright notice.

## Authors

[lyranie](https://github.com/lyranie/)

## License

[GPL 3.0](https://www.gnu.org/licenses/gpl-3.0)


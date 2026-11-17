# CraftsMineNKM (Beta)

> *Formerly known as **Barrel CraftsMine***

Native protocol translation plugin for **Nukkit-MOT** extending server compatibility from **Protocol 113 (MCPE 1.1.0)** to **Protocol 80 (MCPE 0.15.0)**, allowing classic Minecraft Pocket Edition clients to play alongside modern versions without the need for external proxies and with zero overhead for native clients (`>=1.1.0`).

---

## History and Architecture

Originally conceived as a standalone proxy (**Barrel CraftsMine**), the project evolved into a **100% native architecture as a plugin for Nukkit-MOT**. When running directly on the network stack and server world engine, it eliminates the double serialization of RakNet, reduces latency and ensures ideal stable sessions for lobbies, mini-games (TNT Run, TNT Tag, Parkour) and multiversion servers.

### Credits and Technical References
**Development and Architecture:** Creators Program (Trollhunters501) assisted by code engineering agents in **Google AI Studio**.
**Reverse Engineering and Protocol Reference:** Based on the package structure and network mapping of [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (MoutshetMine reference code) and MCPE legacy protocol specifications.

---

## Protocols Supported (80 – 113+)

| Protocol | MCPE Version | RakNet | Translation Strategy |
|------------------------------------------------------------------------------------------------------------------------------------------------------
| `80 - 84` | `0.15.0` – `0.15.10` | | `v7` | Fixed coding (BE/LE), without VarInts, 128-column chunks, ResourcePacks bypass and legacy block/biome remapping |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | | Varints initials, IDs `PacketsIds91`, chunks with classic ordering and ARGB biomes |
| `92 - 101` | `1.0.0` – `1.0.4.0` | | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` with prefix `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` tablet |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | | | | | IDs `PacketsIds110`, adaptation of `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Nukkit-MOT** Native (automatic decoupling without performance impact) |

---

## Main Features

***Real Time StraightBridge:** Intercept and translate incoming and outgoing packages only for players connected from legacy versions (`< 113`), leaving the flow of modern players intact.
* **Chunks and World adaptation:** Automatic conversion of modern subchunks to flat block and biome format supported by MCPE 0.15.x, remapping block IDs after 0.15 to avoid forced closures in the client.
* **Chat and Legacy Commands compatibility:** Two-way message translation and transparent execution of commands sent in plain text format from 0.15.x clients.
* **Centralized Settings:** Total bridge behavior control, packet diagnosis and compatibility settings from `config.yml`.

---

## Installation

1. Download or compile the plugin `.jar` file.
2. Place `CraftsmineNKM-1.0.0.jar` inside your server's `plugins/` folder **Nukkit-MOT**.
3. Start or restart the server to generate the `plugins/CraftsmineNKM/config.yml` configuration file.
4. Make sure you have strict version/Xbox Live verification disabled or adjusted in `server.properties` if you want to allow free access from classic 0.15.x customers.

---

## Compilation from Source Code

Requires **Java (JDK 17+)** and **Apache Maven**:

```bash
mvn clean package
````

The compiled artifact will be generated in:

````
target/CraftsmineNKM-1.0.0.jar
````

# Project Status and Notes
- Optimized primarily for MCPE 0.15.10 (Protocol 84) in mini-game and Overworld environments.
- It is recommended to restrict access to modern dimensions (such as the updated Nether) or use quick reappearance rules ([Fast Respawn] (https://cloudburstmc.org/resources/fastrespawn.261/)) to ensure maximum stability in low-income customers.

## Characteristics that cause Crash (Known)
- Reappear
- Open inventory
- Some Entities
- Dimensions unrelated to the main dimension (end, nether)
## Missing features
- Commands
- Skins of other players
- And more...
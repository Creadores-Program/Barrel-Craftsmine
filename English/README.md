# CraftsMineNKM

> *Previously known as **Barrel CraftsMine***

A native protocol translation plugin for **Nukkit-MOT** that extends server compatibility from **Protocol 113 (MCPE 1.1.0)** down to **Protocol 80 (MCPE 0.15.0)**, allowing classic Minecraft Pocket Edition clients to play alongside modern versions without external proxies and with zero overhead for native clients (`>= 1.1.0`).

---

## History and Architecture

Originally conceived as an independent proxy (**Barrel CraftsMine**), the project evolved into a **100% native architecture as a plugin for Nukkit-MOT**. By running directly on the server's network stack and world engine, it eliminates double RakNet serialization, reduces latency, and guarantees stable sessions ideal for lobbies, minigames (TNT Run, TNT Tag, Parkour), and multi-version servers.

### Credits and Technical References
* **Development and Architecture:** Creadores Program (Trollhunters501) assisted by code engineering agents in **Google AI Studio**.
* **Reverse Engineering and Protocol Reference:** Based on package structures and network mappings from [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (reference code for multi-version PocketMine) and legacy MCPE protocol specifications.

---

## Supported Protocols (80 – 113+)

| Protocol | MCPE Version | RakNet | Translation Strategy |
|----------|--------------|--------|----------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Fixed encoding (BE/LE), no VarInts, 128-column chunks, ResourcePacks bypass, and legacy block/biome remapping |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | Initial VarInts, `PacketsIds91` IDs, chunks with classic ordering + ARGB biomes |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | XZY SubChunks, `PacketsIds100` IDs, `BatchPacket` with `0x06` prefix |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | `PacketsIds102` / `PacketsIds105` IDs, compressed `LoginPacket` |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | `PacketsIds110` IDs, `MovePlayerPacket` (`ridingEid`) adaptation |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Natively handled by Nukkit-MOT** (automatic decoupling with zero performance impact) |

---

## Key Features

* **Direct Real-Time Bridge:** Intercepts and translates incoming and outgoing packets exclusively for legacy clients (`< 113`), leaving the data flow for modern players completely untouched.
* **Chunk and World Adaptation:** Automatic conversion of modern subchunks into the flat block and biome format supported by MCPE 0.15.x, remapping post-0.15 block IDs to prevent client crashes.
* **Legacy Chat and Command Compatibility:** Bidirectional message translation and transparent execution of text-format commands sent from 0.15.x clients.
* **Centralized Configuration:** Full control over bridge behavior, packet diagnostics, and compatibility settings directly from `config.yml`.

---

## Installation

1. Download or compile the plugin `.jar` file.
2. Place `CraftsmineNKM-1.0.0.jar` inside the `plugins/` folder of your **Nukkit-MOT** server.
3. Start or restart the server to generate the configuration file at `plugins/CraftsmineNKM/config.yml`.
4. Ensure strict version or Xbox Live verification is disabled or adjusted in `server.properties` if you wish to allow free access from classic 0.15.x clients.

---

## Compilation from Source

Requires **Java (JDK 17+)** and **Apache Maven**:

```bash
mvn clean package
```

The compiled artifact will be generated at:
```text
target/CraftsmineNKM-1.0.0.jar
```

---

## Project Status and Notes

* Optimized primarily for **MCPE 0.15.10 (Protocol 84)** in minigame environments and Overworld worlds.
* It is recommended to restrict access to modern dimensions (such as the updated Nether) or use *Fast Respawn* rules to ensure maximum stability on low-resource clients.

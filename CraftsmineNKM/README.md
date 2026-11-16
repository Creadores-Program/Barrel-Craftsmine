# CraftsmineNKM

Plugin multiversion para **Nukkit-MOT** que extiende la compatibilidad oficial del servidor desde **Protocol 113 (MCPE 1.1.0)** hasta **Protocol 80 (MCPE 0.15.0)**, garantizando estabilidad, cero sobrecarga para clientes modernos (`>= 1.1.0`) y monitoreo en tiempo real mediante `config.yml`.

## Protocolos Soportados (80 - 113)

| Protocolo | Versión MCPE | RakNet | Estrategia de Puente |
|-----------|--------------|--------|----------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Codificación fija (BE/LE), sin VarInts, chunks 128-col, bypass de ResourcePacks |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | VarInts iniciales, IDs `PacketsIds91`, chunks `order` + biome ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` prefijo `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` comprimido |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | IDs `PacketsIds110`, `MovePlayerPacket` ridingEid |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | Nativo de Nukkit-MOT (desacoplamiento automático sin latencia) |

## Compilación con Maven

```bash
mvn clean package
```

El artefacto compilado se genera en `target/CraftsmineNKM-1.0.0.jar`.

## Comandos

- `/craftsmine status` — Muestra estado del puente, sesiones legadas activas y métricas.
- `/craftsmine debug <on|off>` — Activa o desactiva el modo Debug en caliente y actualiza `config.yml`.
- `/craftsmine reload` — Recarga `config.yml` sin reiniciar el servidor.
- `/craftsmine logs [líneas]` — Muestra las últimas líneas del monitor en tiempo real.
- `/craftsmine protocols` — Lista la tabla de protocolos 80–113 y sus familias.
- `/craftsmine sessions` — Lista los jugadores conectados usando protocolos legados.

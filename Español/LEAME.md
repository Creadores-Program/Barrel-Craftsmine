# CraftsMineNKM (Beta)

> *Anteriormente conocido como **Barrel CraftsMine***

Plugin nativo de traducción de protocolos para **Nukkit-MOT** que extiende la compatibilidad del servidor desde **Protocolo 113 (MCPE 1.1.0)** hasta **Protocolo 80 (MCPE 0.15.0)**, permitiendo que clientes clásicos de Minecraft Pocket Edition jueguen junto a versiones modernas sin necesidad de proxies externos y con cero sobrecarga para clientes nativos (`>= 1.1.0`).

---

## Historia y Arquitectura

Originalmente concebido como un proxy independiente (**Barrel CraftsMine**), el proyecto evolucionó hacia una arquitectura **100% nativa como plugin para Nukkit-MOT**. Al ejecutarse directamente sobre la pila de red y el motor de mundo del servidor, elimina la doble serialización de RakNet, reduce la latencia y garantiza sesiones estables ideales para lobbies, minijuegos (TNT Run, TNT Tag, Parkour) y servidores multiversión.

### Créditos y Referencias Técnicas
* **Desarrollo y Arquitectura:** Creadores Program (Trollhunters501) asistido mediante agentes de ingeniería de código en **Google AI Studio**.
* **Ingeniería Inversa y Referencia de Protocolo:** Basado en la estructura de paquetes y mapeos de red de [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (código de referencia de PocketMine multiversión) y especificaciones de protocolos legados de MCPE.

---

## Protocolos Soportados (80 – 113+)

| Protocolo | Versión MCPE | RakNet | Estrategia de Traducción |
|-----------|--------------|--------|--------------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Codificación fija (BE/LE), sin VarInts, chunks de 128 columnas, bypass de ResourcePacks y remapeo de bloques/biomas legados |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | VarInts iniciales, IDs `PacketsIds91`, chunks con ordenamiento clásico y biomas ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` con prefijo `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` comprimido |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | IDs `PacketsIds110`, adaptación de `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Nativo de Nukkit-MOT** (desacoplamiento automático sin impacto en rendimiento) |

---

## Características Principales

* **Puente Directo en Tiempo Real:** Intercepta y traduce paquetes entrantes y salientes únicamente para los jugadores conectados desde versiones legadas (`< 113`), dejando intacto el flujo de los jugadores modernos.
* **Adaptación de Chunks y Mundo:** Conversión automática de subchunks modernos al formato plano de bloques y biomas soportado por MCPE 0.15.x, remapeando IDs de bloques posteriores a la 0.15 para evitar cierres forzosos en el cliente.
* **Compatibilidad de Chat y Comandos Legados:** Traducción bidireccional de mensajes y ejecución transparente de comandos enviados en formato texto plano desde clientes 0.15.x.
* **Configuración Centralizada:** Control total del comportamiento del puente, diagnóstico de paquetes y ajustes de compatibilidad desde `config.yml`.

---

## Instalación

1. Descarga o compila el archivo `.jar` del plugin.
2. Coloca `CraftsmineNKM-1.0.0.jar` dentro de la carpeta `plugins/` de tu servidor **Nukkit-MOT**.
3. Inicia o reinicia el servidor para generar el archivo de configuración `plugins/CraftsmineNKM/config.yml`.
4. Asegúrate de tener desactivada o ajustada la verificación estricta de versión/Xbox Live en `server.properties` si deseas permitir el acceso libre desde clientes clásicos 0.15.x.

---

## Compilación desde el Código Fuente

Requiere **Java (JDK 17+)** y **Apache Maven**:

```bash
mvn clean package
```

El artefacto compilado se generará en:

```
target/CraftsmineNKM-1.0.0.jar
```

# Estado del Proyecto y Notas
- Optimizado principalmente para MCPE 0.15.10 (Protocolo 84) en entornos de minijuegos y mundos Overworld.
- Se recomienda restringir el acceso a dimensiones modernas (como el Nether actualizado) o utilizar reglas de reaparición rápida ([Fast Respawn](https://cloudburstmc.org/resources/fastrespawn.261/)) para garantizar la máxima estabilidad en clientes de bajos recursos.

## Caracteristicas que causan Crash (Conocidas)
- Reaparecer
- Abrir inventario
- Algunas Entidades
- Dimensiones ajenas a la dimención principal (end, nether)
## Caracteristicas faltantes
- Comandos
- Skins de otros Jugadores
- Y más...
# CraftsMineNKM (Beta)

> *Précédemment connu sous le nom de **Barrel CraftsMine***

Un plugin natif de traduction de protocoles pour **Nukkit-MOT** qui étend la compatibilité du serveur du **Protocole 113 (MCPE 1.1.0)** jusqu'au **Protocole 80 (MCPE 0.15.0)**, permettant aux clients classiques de Minecraft Pocket Edition de jouer aux côtés de versions modernes sans proxy externe et avec zéro surcharge pour les clients natifs (`>= 1.1.0`).

---

## Histoire et Architecture

Initialement conçu comme un proxy indépendant (**Barrel CraftsMine**), le projet a évolué vers une architecture **100 % native en tant que plugin pour Nukkit-MOT**. En s'exécutant directement sur la pile réseau et le moteur de monde du serveur, il élimine la double sérialisation RakNet, réduit la latence et garantit des sessions stables, idéales pour les lobbys, les mini-jeux (TNT Run, TNT Tag, Parkour) et les serveurs multiversion.

### Crédits et Références Techniques
* **Développement et Architecture :** Creadores Program (Trollhunters501) assisté par des agents d'ingénierie de code sur **Google AI Studio**.
* **Rétro-ingénierie et Référence de Protocole :** Basé sur les structures de paquets et les mappages réseau de [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (code de référence PocketMine multiversion) et les spécifications de protocoles hérités de MCPE.

---

## Protocoles Pris en Charge (80 – 113+)

| Protocole | Version MCPE | RakNet | Stratégie de Traduction |
|-----------|--------------|--------|-------------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Encodage fixe (BE/LE), pas de VarInts, chunks de 128 colonnes, contournement des ResourcePacks et remappage des blocs/biomes hérités |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | VarInts initiaux, IDs `PacketsIds91`, chunks avec ordre classique et biomes ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` avec préfixe `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` compressé |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | IDs `PacketsIds110`, adaptation de `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Géré nativement par Nukkit-MOT** (découplage automatique sans impact sur les performances) |

---

## Caractéristiques Principales

* **Pont Direct en Temps Réel :** Intercepte et traduit les paquets entrants et sortants exclusivement pour les clients hérités (`< 113`), laissant le flux de données pour les joueurs modernes totalement intact.
* **Adaptation des Chunks et du Monde :** Conversion automatique des subchunks modernes au format plat de blocs et de biomes pris en charge par MCPE 0.15.x, en remappant les IDs de blocs postérieurs à 0.15 pour éviter les plantages du client.
* **Compatibilité Chat et Commandes Héritées :** Traduction bidirectionnelle des messages et exécution transparente des commandes envoyées au format texte brut depuis les clients 0.15.x.
* **Configuration Centralisée :** Contrôle total du comportement du pont, des diagnostics de paquets et des paramètres de compatibilité directement depuis `config.yml`.

---

## Installation

1. Téléchargez ou compilez le fichier `.jar` du plugin.
2. Placez `CraftsmineNKM-1.0.0.jar` dans le dossier `plugins/` de votre serveur **Nukkit-MOT**.
3. Démarrez ou redémarrez le serveur pour générer le fichier de configuration dans `plugins/CraftsmineNKM/config.yml`.
4. Assurez-vous que la vérification stricte de la version ou de Xbox Live est désactivée ou ajustée dans `server.properties` si vous souhaitez autoriser l'accès libre depuis les clients classiques 0.15.x.

---

## Compilation à partir du Code Source

Requiert **Java (JDK 17+)** et **Apache Maven** :

```bash
mvn clean package
```

L'artefact compilé sera généré dans :
```text
target/CraftsmineNKM-1.0.0.jar
```

---

## Statut du Projet et Remarques

* Optimisé principalement pour **MCPE 0.15.10 (Protocole 84)** dans des environnements de mini-jeux et des mondes Overworld.
* Il est recommandé de restreindre l'accès aux dimensions modernes (comme le Nether mis à jour) ou d'utiliser des règles de réapparition rapide (*Fast Respawn*) pour garantir une stabilité maximale sur les clients à faibles ressources.

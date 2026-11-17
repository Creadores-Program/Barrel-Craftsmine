# CraftsMineNKM (Beta)

> *Precedentemente conosciuto come **Barrel CraftsMine***

Plugin di traduzione del protocollo nativo per **Nukkit-MOT** che estende la compatibilità del server da **Protocol 113 (MCPE 11.0)** a **Protocol 80 (MCPE 0.15.0)**, consentendo ai classici client di Minecraft Pocket Edition di giocare insieme alle versioni moderne senza la necessità di proxy esterni e con zero overhead per i client nativi (`>=1.1.0`).

---

## Storia e architettura

Originariamente concepito come proxy autonomo (**Barrel CraftsMine**), il progetto si è evoluto in un'architettura nativa **100% come plugin per Nukkit-MOT**. Quando si esegue direttamente sullo stack di rete e sul motore del mondo del server, elimina la doppia serializzazione di RakNet, riduce la latenza e garantisce sessioni stabili ideali per lobby, mini-giochi (TNT Run, TNT Tag, Parkour) e server multiversione.

### Crediti e riferimenti tecnici
**Sviluppo e architettura:** Programma Creators (Trollhunters501) assistito da agenti di ingegneria del codice in **Google AI Studio**.
**Reverse Engineering and Protocol Reference:** In base alla struttura del pacchetto e alla mappatura di rete di [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (codice di riferimento PocketMine) e specifiche del protocollo legacy MCPE.

---

## Protocolli Supportati (80 – 113+)

| Protocollo | Versione MCPE | RakNet | Strategia di traduzione |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| `80 - 84` | `0.15.0` – `0.15.10` | | `v7` | Codifica fissa (BE/LE), senza Varinti, pezzi di 128 colonne, bypass ResourcePacks e remapping di blocchi/biome legacy |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | | Iniziali di Varinti, ID `PacketsIds91`, pezzi con ordini classici e biomi ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` con prefisso `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` tablet |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | | | | | | | IDs `PacketsIds110`, adattamento di `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Nukkit-MOT** Nativo (disaccoppiamento automatico senza impatto sulle prestazioni) |

---

## Caratteristiche principali

***Real Time StraightBridge:** Intercettare e tradurre pacchetti in entrata e in uscita solo per i giocatori collegati da versioni legacy (`< 113`), lasciando intatto il flusso dei giocatori moderni.
* **Chunks e adattamento mondiale:** Conversione automatica dei moderni sottogruppi in formato flat block e biome supportato da MCPE 0.15.x, rimappatura degli ID del blocco dopo 0,15 per evitare chiusure forzate nel client.
* **Chat e compatibilità dei comandi legacy:** Traduzione di messaggi bidirezionali e esecuzione trasparente dei comandi inviati in formato testo normale dai client 0.15.x.
* **Contestazioni centralizzate:** Controllo totale del comportamento del ponte, diagnosi dei pacchetti e impostazioni di compatibilità da `config.yml`.

---

## Installazione

1. Scarica o compila il file plugin `.jar`.
2. Posizionare `CraftsmineNKM-1.0.0.jar` all'interno della cartella `plugins/` del server **Nukkit-MOT**.
3. Avviare o riavviare il server per generare il file di configurazione `plugins/CraftsmineNKM/config.yml`.
4. Assicurati di avere una rigorosa verifica di versione/Xbox Live disabilitata o regolata in `server.properties` se vuoi consentire l'accesso gratuito dai clienti classici 0.15.x.

---

## Compilazione dal codice sorgente

Richiede **Java (JDK 17+)** e **Apache Maven**:

```bash
pacchetto pulito mvn
````

L'artefatto compilato sarà generato in:

````
target/CraftsmineNKM-1.0.0.jar
````

# Stato del progetto e note
- Ottimizzato principalmente per MCPE 0.15.10 (Protocol 84) in ambienti di mini-gioco e Overworld.
- Si consiglia di limitare l'accesso alle dimensioni moderne (come il Nether aggiornato) o utilizzare regole di ricomparsa rapida ([Fast Respawn] (https://cloudburstmc.org/resources/fastrespawn.261/)) per garantire la massima stabilità nei clienti a basso reddito.

## Caratteristiche che causano Crash (noto)
- Riapparire
- inventario aperto
- Alcune Entità
- Dimensioni non correlate alla dimensione principale (fine, né né)
## Caratteristiche mancanti
- Comandi
- Skin di altri giocatori
- E di più...
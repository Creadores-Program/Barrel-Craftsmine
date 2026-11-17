# CraftsMineNKM (Beta)

> *Precedentemente noto come **Barrel CraftsMine***

Un plugin nativo di traduzione dei protocolli per **Nukkit-MOT** che estende la compatibilità del server dal **Protocollo 113 (MCPE 1.1.0)** fino al **Protocollo 80 (MCPE 0.15.0)**, consentendo ai client classici di Minecraft Pocket Edition di giocare insieme alle versioni moderne senza proxy esterni e con zero sovraccarico per i client nativi (`>= 1.1.0`).

---

## Storia e Architettura

Originariamente concepito come un proxy indipendente (**Barrel CraftsMine**), il progetto si è evoluto in un'architettura **100% nativa come plugin per Nukkit-MOT**. Eseguendosi direttamente sullo stack di rete e sul motore di gioco del server, elimina la doppia serializzazione di RakNet, riduce la latenza e garantisce sessioni stabili, ideali per lobby, minigiochi (TNT Run, TNT Tag, Parkour) e server multiversione.

### Crediti e Riferimenti Tecnici
* **Sviluppo e Architettura:** Creadores Program (Trollhunters501) assistito da agenti di ingegneria del codice su **Google AI Studio**.
* **Reverse Engineering e Riferimento del Protocollo:** Basato sulle strutture dei pacchetti e sulle mappature di rete di [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (codice di riferimento di PocketMine multiversione) e sulle specifiche dei protocolli legacy di MCPE.

---

## Protocolli Supportati (80 – 113+)

| Protocollo | Versione MCPE | RakNet | Strategia di Traduzione |
|------------|---------------|--------|-------------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Codifica fissa (BE/LE), nessun VarInt, chunk a 128 colonne, bypass dei ResourcePack e rimappatura di blocchi/biomi legacy |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | VarInt iniziali, ID `PacketsIds91`, chunk con ordinamento classico e biomi ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | SubChunk XZY, ID `PacketsIds100`, `BatchPacket` con prefisso `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | ID `PacketsIds102` / `PacketsIds105`, `LoginPacket` compresso |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | ID `PacketsIds110`, adattamento di `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Gestito nativamente da Nukkit-MOT** (disaccoppiamento automatico senza impatto sulle prestazioni) |

---

## Caratteristiche Principali

* **Ponte Diretto in Tempo Reale:** Intercetta e traduce i pacchetti in entrata e in uscita esclusivamente per i client legacy (`< 113`), lasciando completamente intatto il flusso di dati per i giocatori moderni.
* **Adattamento di Chunk e Mondo:** Conversione automatica dei subchunk moderni nel formato piatto di blocchi e biomi supportato da MCPE 0.15.x, rimappando gli ID dei blocchi successivi alla 0.15 per evitare crash del client.
* **Compatibilità Chat e Comandi Legacy:** Traduzione bidirezionale dei messaggi ed esecuzione trasparente dei comandi inviati in formato testo semplice dai client 0.15.x.
* **Configurazione Centralizzata:** Controllo completo sul comportamento del ponte, diagnostica dei pacchetti e impostazioni di compatibilità direttamente dal file `config.yml`.

---

## Installazione

1. Scarica o compila il file `.jar` del plugin.
2. Posiziona `CraftsmineNKM-1.0.0.jar` all'interno della cartella `plugins/` del tuo server **Nukkit-MOT**.
3. Avvia o riavvia il server per generare il file di configurazione in `plugins/CraftsmineNKM/config.yml`.
4. Assicurati che la verifica rigorosa della versione o di Xbox Live sia disattivata o regolata in `server.properties` se desideri consentire il libero accesso dai client classici 0.15.x.

---

## Compilazione dal Codice Sorgente

Richiede **Java (JDK 17+)** e **Apache Maven**:

```bash
mvn clean package
```

L'artefatto compilato verrà generato in:
```text
target/CraftsmineNKM-1.0.0.jar
```

---

## Stato del Progetto e Note

* Ottimizzato principalmente per **MCPE 0.15.10 (Protocollo 84)** in ambienti minigame e mondi Overworld.
* Si consiglia di limitare l'accesso alle dimensioni moderne (come il Nether aggiornato) o di utilizzare regole di rientro rapido (*Fast Respawn*) per garantire la massima stabilità sui client con risorse limitate.

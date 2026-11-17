# ArtesanatoMineNKM (Beta)

> *Anteriormente conhecido como **Barrel CraftsMine***

Plugin de tradução de protocolo nativo para **Nukkit-MOT** estendendo a compatibilidade do servidor de **Protocolo 113 (MCPE 1.1.0)** para **Protocolo 80 (MCPE 0.15.0)**, permitindo que os clientes clássicos do Minecraft Pocket Edition joguem ao lado de versões modernas sem a necessidade de proxies externos e com zero sobrecarga para clientes nativos (`>=1.1.0`).

----

## História e Arquitetura

Originalmente concebido como um proxy autônomo (**Barrel CraftsMine**), o projeto evoluiu para uma arquitetura nativa de ** 100% como um plugin para Nukkit-MOT**. Ao ser executado diretamente na pilha de rede e no motor do mundo do servidor, elimina a dupla serialização do RakNet, reduz a latência e garante sessões estáveis ideais para lobbies, mini-jogos (TNT Run, TNT Tag, Parkour) e servidores multiversão.

### Créditos e Referências Técnicas
**Desenvolvimento e Arquitetura:** Programa de Criadores (Trollhunters501) assistido por agentes de engenharia de código em **Google AI Studio**.
**Reverse Engineering and Protocol Reference:** Baseado na estrutura do pacote e no mapeamento de rede de [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (Código de referência PocketMine) e especificações de protocolo legado MCPE.

----

Protocolos ## Suportados (80 – 113+)

| Protocolo | Versão MCPE | RakNet | Estratégia de tradução |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| `80 - 84` | `0.15.0` – `0.15.10` | | | `v7` | | Codificação fixa (BE/LE), sem VarInts, pedaços de 128 colunas, bypass ResourcePacks e remapeamento de bloco/biome legado |
| `90 - 91` | `0.15.90` – `0.17.0` | `.17.0` | | Varints iniciais, IDs `PacketsIds91`, pedaços com ordenação clássica e biomas ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | | | `v8` | | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` com prefixo `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | | `.0.9` | | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` tablet |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | | | | | | | | | IDs `PacketsIds110`, adaptação de `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | | **Nukkit-MOT** Nativo (desacoplamento automático sem impacto de desempenho) |

----

## Principais Características

***Real Time StraightBridge:** Interceptar e traduzir pacotes de entrada e saída apenas para jogadores conectados a partir de versões (`< 113` legadas), deixando o fluxo de jogadores modernos intactos.
* **Chunks e adaptação mundial:** Conversão automática de subpedrões modernos para bloco plano e formato de biomame suportado pelo MCPE 0.15.x, remapeando IDs de bloco após 0,15 para evitar fechamentos forçados no cliente.
**Compatibilidade com comandos de bate-papo e legado:** tradução de mensagens bidirecionais e execução transparente de comandos enviados em formato de texto simples a partir de clientes 0.15.x.
* **Configurações centralizadas:** Controle de comportamento de ponte total, diagnóstico de pacotes e configurações de compatibilidade de `config.yml`.

----

## Instalação

1. 1. Baixe ou compile o arquivo do plugin `.jar`.
2. 2. Coloque `CraftsmineNKM-1.0.0.jar` dentro da pasta `plugins/` do seu servidor **Nukkit-MOT**.
3. Inicie ou reinicie o servidor para gerar o arquivo de configuração `plugins/CraftsmineNKM/config.yml`.
4. 4. Certifique-se de ter a versão rigorosa / Xbox Live desativada ou ajustada em `server.properties` se você quiser permitir o acesso gratuito de clientes clássicos 0.15.x.

----

## Compilação do Código Fonte

Requer **Java (JDK 17+)** e **Apache Maven**:

```bash
mvn pacote limpo
`````

O artefato compilado será gerado em:

`````
alvo/CraftsmineNKM-1.0.0.jar
`````

# Status do Projeto e Notas
- Otimizado principalmente para MCPE 0.15.10 (Protocolo 84) em ambientes de mini-jogo e Overworld.
- Recomenda-se restringir o acesso a dimensões modernas (como o Nether atualizado) ou usar regras de reaparecimento rápido ([Fast Respawn] (https://cloudburstmc.org/resources/fastrespawn.261/)) para garantir a máxima estabilidade em clientes de baixa renda.

## Características que causam Crash (Conhecido)
- Reaparecer
- Inventário aberto
- Algumas Entidades
- Dimensões não relacionadas à dimensão principal (fim, internet)
## Recursos ausentes
- Comandos
- Skins de outros jogadores
- E mais...
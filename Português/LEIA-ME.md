# CraftsMineNKM

> *Anteriormente conhecido como **Barrel CraftsMine***

Um plugin nativo de tradução de protocolos para o **Nukkit-MOT** que estende a compatibilidade do servidor desde o **Protocolo 113 (MCPE 1.1.0)** até o **Protocolo 80 (MCPE 0.15.0)**, permitindo que clientes clássicos do Minecraft Pocket Edition joguem junto com versões modernas sem proxies externos e com sobrecarga zero para clientes nativos (`>= 1.1.0`).

---

## Histórico e Arquitetura

Originalmente concebido como um proxy independente (**Barrel CraftsMine**), o projeto evoluiu para uma arquitetura **100% nativa como plugin para o Nukkit-MOT**. Ao rodar diretamente sobre a pilha de rede e o motor de mundo do servidor, ele elimina a dupla serialização do RakNet, reduz a latência e garante sessões estáveis, ideais para lobbies, minigames (TNT Run, TNT Tag, Parkour) e servidores multiversão.

### Créditos e Referências Técnicas
* **Desenvolvimento e Arquitetura:** Creadores Program (Trollhunters501) auxiliado por agentes de engenharia de código no **Google AI Studio**.
* **Engenharia Reversa e Referência de Protocolo:** Baseado nas estruturas de pacotes e mapeamentos de rede do [MySoft-0.15.x-1.21.x](https://github.com/Dr1xyDev/MySoft-0.15.x-1.21.x) (código de referência de PocketMine multiversión) e especificações de protocolos legados do MCPE.

---

## Protocolos Suportados (80 – 113+)

| Protocolo | Versão MCPE | RakNet | Estratégia de Tradução |
|-----------|-------------|--------|------------------------|
| `80 - 84` | `0.15.0` – `0.15.10` | `v7` | Codificação fixa (BE/LE), sem VarInts, chunks de 128 colunas, bypass de ResourcePacks e remapeamento de blocos/biomas legados |
| `90 - 91` | `0.15.90` – `0.17.0` | `v8` | VarInts iniciais, IDs `PacketsIds91`, chunks com ordenação clássica e biomas ARGB |
| `92 - 101` | `1.0.0` – `1.0.4.0` | `v8` | SubChunks XZY, IDs `PacketsIds100`, `BatchPacket` com prefixo `0x06` |
| `102 - 107` | `1.0.4` – `1.0.9` | `v8` | IDs `PacketsIds102` / `PacketsIds105`, `LoginPacket` compactado |
| `110 - 112` | `1.1.0.0` – `1.1.0.9` | `v8` | IDs `PacketsIds110`, adaptação de `MovePlayerPacket` (`ridingEid`) |
| `113+` | `1.1.0` – `1.26.x` | `v8-v11` | **Manipulado nativamente pelo Nukkit-MOT** (desacoplamento automático sem impacto de desempenho) |

---

## Principais Recursos

* **Ponte Direta em Tempo Real:** Intercepta e traduz pacotes de entrada e saída exclusivamente para clientes legados (`< 113`), mantendo o fluxo de dados para jogadores modernos completamente intacto.
* **Adaptação de Chunks e Mundo:** Conversão automática de subchunks modernos para o formato plano de blocos e biomas suportado pelo MCPE 0.15.x, remapeando IDs de blocos posteriores ao 0.15 para evitar falhas (*crashes*) no cliente.
* **Compatibilidade com Chat e Comandos Legados:** Tradução bidirecional de mensagens e execução transparente de comandos em formato de texto simples enviados por clientes 0.15.x.
* **Configuração Centralizada:** Controle total sobre o comportamento da ponte, diagnósticos de pacotes e ajustes de compatibilidade diretamente a partir do arquivo `config.yml`.

---

## Instalação

1. Baixe ou compile o arquivo `.jar` do plugin.
2. Coloque `CraftsmineNKM-1.0.0.jar` dentro da pasta `plugins/` do seu servidor **Nukkit-MOT**.
3. Inicie ou reinicie o servidor para gerar o arquivo de configuração em `plugins/CraftsmineNKM/config.yml`.
4. Certifique-se de que a verificação estrita de versão ou do Xbox Live esteja desativada ou ajustada no arquivo `server.properties` caso deseje permitir o acesso livre de clientes clássicos 0.15.x.

---

## Compilação a partir do Código-Fonte

Requer **Java (JDK 17+)** e **Apache Maven**:

```bash
mvn clean package
```

O artefato compilado será gerado em:
```text
target/CraftsmineNKM-1.0.0.jar
```

---

## Status do Projeto e Observações

* Otimizado principalmente para **MCPE 0.15.10 (Protocolo 84)** em ambientes de minigames e mundos do tipo Overworld.
* Recomenda-se restringir o acesso a dimensões modernas (como o Nether atualizado) ou utilizar regras de reaparecimento rápido (*Fast Respawn*) para garantir a máxima estabilidade em clientes de baixo desempenho.

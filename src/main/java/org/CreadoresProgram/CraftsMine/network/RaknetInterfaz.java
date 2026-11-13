package org.CreadoresProgram.CraftsMine.network;
import java.net.InetSocketAddress;
import lombok.Getter;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.RakNet;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.EncapsulatedPacket;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.server.RakNetServer;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.server.ServerHandler;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.server.ServerInstance;
import org.CreadoresProgram.CraftsMine.server.Server;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.ProtocolInfo;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.BatchPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.Binary;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.Protocol;
import org.CreadoresProgram.CraftsMine.nukkitLib.raknet.protocol.packet.PING_DataPacket;
import org.CreadoresProgram.CraftsMine.network.protocol.UnknownPacket;
import java.util.Arrays;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.CreadoresProgram.CraftsMine.utils.QueryTask;
import org.CreadoresProgram.CraftsMine.meJustinLib.bedrockserverquery.data.BedrockQuery;
public class RaknetInterfaz implements ServerInstance{
  @Getter
  private final Server server;
  @Getter
  private final RakNetServer rakServer;
  private String motd;
  private String submotd;
  @Getter
  private final ServerHandler handler;

  @Getter
  private HashMap<String, byte[]> tokens = new HashMap<String, byte[]>();

  private final long serverId = ThreadLocalRandom.current().nextLong();

  public RaknetInterfaz(Server server, String ip, int port){
    this.server = server;
    rakServer = new RakNetServer(this.server.getLogger(), port, ip);
    handler = new ServerHandler(rakServer, this);
  }
  public void setMOTD(String motdn){
    this.setMOTD(motdn, "CraftsMine Software by Creadores Program");
  }
  public void setMOTD(String motdn, String submotdn){
    if(this.server.getConfig().isSynchServer()){
      BedrockQuery infoServ = QueryTask.getInfoServ();
      String name = "MCPE;";
      if(infoServ.online){
        name += infoServ.motd.replaceAll(";", "\\;") + ";";
        name += ProtocolInfo.CURRENT_PROTOCOL + ";";
        name += ProtocolInfo.MINECRAFT_VERSION_NETWORK + ";";
        name += infoServ.playerCount+";";
        name += Integer.toString(infoServ.maxPlayers) + ";";
        name += Long.toString(this.serverId)+";";
        name += infoServ.software.replaceAll(";", "\\;") + ";";
        name += infoServ.gamemode+";";
        name += "1;";
      }else{
        name += "Offline;";
        name += ProtocolInfo.CURRENT_PROTOCOL + ";";
        name += ProtocolInfo.MINECRAFT_VERSION_NETWORK + ";";
        name += "0;";
        name += "1;";
        name += Long.toString(this.serverId)+";";
        name += submotdn.replaceAll(";", "\\;") + ";";
        name += "Survival;";
        name += "1;";
      }
      if(handler != null){
        handler.sendOption("name", name);
      }
    }else{
      String name = "MCPE;";
      name += motdn.replaceAll(";", "\\;") + ";";
      name += ProtocolInfo.CURRENT_PROTOCOL + ";";
      name += ProtocolInfo.MINECRAFT_VERSION_NETWORK + ";";
      name += this.server.getBedrockPlayers().size()+";";
      name += Integer.toString(this.server.getConfig().getMaxplayers()) + ";";
      name += Long.toString(this.serverId)+";";
      name += submotdn.replaceAll(";", "\\;") + ";";
      name += "Survival;";
      name += "1;";
      if(handler != null){
        handler.sendOption("name", name);
      }
      this.motd = motdn;
      this.submotd = submotdn;
    }
  }
  public void onTick(){
    while(handler.handlePacket()){
      
    }
  }
  @Override
  public void openSession(String identifier, String address, int port, long clienID){
    if(this.server.getBedrockPlayers().containsKey(identifier)){
      DisconnectPacket disc = new DisconnectPacket();
      disc.message = "There is already Someone connected to your Account!";
      this.sendPacket(identifier, disc, false);
      return;
    }else{
      this.server.getBedrockPlayers().put(identifier, new Player(identifier));
      this.setMOTD(this.motd, this.submotd);
    }
  }
  @Override
  public void closeSession(String identifier, String reason){
    if(this.server.getBedrockPlayers().containsKey(identifier)){
      Player player = this.server.getBedrockPlayers().get(identifier);
      String name = player.getCraftsmanUsername();
      if(name == null || name.isEmpty()){
        name = "Unknown";
      }
      player.disconnect(reason);
      this.server.getLogger().info(name + " Left the proxy.");
      this.server.getBedrockPlayers().remove(identifier);
    }
    this.setMOTD(this.motd, this.submotd);
  }
  @Override
  public void handleEncapsulated(String identifier, EncapsulatedPacket packet, int flags){
    Player player = this.server.getBedrockPlayers().get(identifier);
    try{
      if(packet.buffer.length > 0){
        if(packet.buffer[0] == PING_DataPacket.ID){
          return;
        }
        MCDPacket pk = Protocol.decode((byte[]) packet.buffer);
        if(pk instanceof UnknownPacket){
          byte[] pack = packet.buffer;
          int offset = 2;
          byte packetType = pack[offset++];
          int sessionID = Binary.readInt(Binary.subBytes(pack, offset, 4));
          offset += 4;
          byte[] payload = Binary.subBytes(pack, offset);
          switch (packetType){
            case (byte) 0x09:
              this.tokens.put(identifier, String.valueOf(new Random().nextInt()).getBytes());
              byte[] reply = Binary.appendBytes(
                (byte) 0x09,
                Binary.writeInt(sessionID),
                this.tokens.get(identifier),
                new byte[]{0x00}
              );
              handler.sendEncapsulated(player.getIdentifierCraftsMan(), EncapsulatedPacket.fromBinary(reply, true));
              break;
            case (byte) 0x00:
              String token = String.valueOf(Binary.readInt(Binary.subBytes(payload, 0, 4)));
              if (!token.equals(new String(this.tokens.get(identifier)))) {
                    break;
              }
              byte[] reply2 = Binary.appendBytes(
                (byte) 0x00,
                Binary.writeInt(sessionID),
                getQuery()
              );
              handler.sendEncapsulated(player.getIdentifierCraftsMan(), EncapsulatedPacket.fromBinary(reply2, true));
              break;
          }
        }
        if(isDebug()){
          this.server.getLogger().debug("|Craftsman size| Recive Datapack "+pk.getClass().getSimpleName() + " 0x"+Binary.bytesToHexString(packet.buffer));
        }
        player.getPacketTranslatorManager().translate(pk);
      }
    }catch(Exception ex){
      this.server.getLogger().logException(ex);
    }
  }
  @Override
  public void notifyACK(String identifier, int identifierACK){
  }
  @Override
  public void handleRaw(String address, int port, byte[] payload){
    handler.sendRaw(address, port, payload);
  }
  @Override
  public void handleOption(String option, String value){
    handler.sendOption(option, value);
  }
  public void shutdown(){
    server.getLogger().info("Disable RakNet 0.15.x...");
    handler.shutdown();
  }
  public void disconnect(String identifier, String reason){
    handler.closeSession(identifier, reason);
  }
  /**
   * Wraps an encoded 0.15.x game packet in an UNCOMPRESSED BatchPacket,
   * matching the wire format that PocketMine/MySoft (RakLibInterface
   * line 271-287) uses for protocol < 110 (which includes 0.15.x):
   *
   * <pre>
   *   [0x06][int32 BE innerLen][ [int32 BE pkLen][pk bytes] ]
   * </pre>
   *
   * <p>RakLibInterface then prepends the 0xFE MCPE_RAKNET_PACKET_ID
   * marker, producing the final encapsulated buffer:
   *
   * <pre>
   *   [0xfe][0x06][int32 BE innerLen][ [int32 BE pkLen][pk bytes] ]
   * </pre>
   *
   * <p><b>CRITICAL</b>: the 0.15.x client REQUIRES every server→client
   * packet to be wrapped in a BatchPacket. MySoft never sends raw
   * encapsulated packets — even a 5-byte PlayStatus(LOGIN_SUCCESS) is
   * wrapped in an (uncompressed) batch. Sending packets raw as
   * {@code [0xfe][pid][payload]} causes the client to silently drop
   * them (the RakNet layer delivers the buffer but the client's game
   * layer doesn't recognise a non-batch encapsulated packet from the
   * server), which manifests as "Locating Server stuck forever with
   * no disconnect" while the v113 server thinks the player is fully
   * connected.</p>
   *
   * <p>Compression is intentionally NOT applied. The previous
   * wrapInBatch() with Zlib.deflate was reverted because some 0.15.x
   * client builds mishandle compressed batches during the login
   * handshake. Uncompressed batches are larger on the wire but
   * unambiguously decodable by every 0.15.x client build tested
   * (verified against MySoft OldBatchPacket.encode() with
   * compressionEnabled=false).</p>
   *
   * @param encoded the encoded packet buffer, starting with its pid byte
   * @return the batch-wrapped buffer {@code [0x06][int32 len][inner stream]},
   *         ready to be prefixed with 0xfe and sent via RakNet
   */
  private static byte[] wrapInUncompressedBatch(byte[] encoded) {
    // Inner stream: [int32 BE pkLen][pk bytes]
    byte[] inner = Binary.appendBytes(Binary.writeInt(encoded.length), encoded);
    // Outer: [0x06][int32 BE innerLen][inner]
    return Binary.appendBytes(
        new byte[]{ProtocolInfo.BATCH_PACKET},
        Binary.writeInt(inner.length),
        inner
    );
  }

  public void sendPacket(String identifier, MCDPacket packet, boolean immediate){
    if(isDebug()){
        this.server.getLogger().debug("|Craftsman size| Send Datapack "+packet.getClass().getSimpleName());
    }
    if(identifier == null || packet == null){
      return;
    }
    boolean overridedImmediate = immediate || packet.isShouldSendImmediate();
    packet.encode();
    EncapsulatedPacket encapsulated = new EncapsulatedPacket();
    encapsulated.buffer = Binary.appendBytes((byte)0xfe, packet.getBuffer());
    encapsulated.needACK = true;
    if(packet.getChannel() == 0){
      encapsulated.reliability = 2;
    }else{
      encapsulated.reliability = 3;
      encapsulated.orderChannel = packet.getChannel();
      encapsulated.orderIndex = 0;
    }
    encapsulated.messageIndex = 0;
    handler.sendEncapsulated(identifier, encapsulated, RakNet.FLAG_NEED_ACK | (overridedImmediate ? RakNet.PRIORITY_IMMEDIATE : RakNet.PRIORITY_NORMAL));
  }
  private boolean isDebug(){
    return this.server.getConfig().isDebug();
  }
  private byte[] getQuery(){
    ByteBuffer query = ByteBuffer.allocate(65536);
        query.put(this.motd.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put("Survival".getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(this.submotd.getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(String.valueOf(this.server.getBedrockPlayers().size()).getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(String.valueOf(this.server.getConfig().getMaxplayers()).getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        query.put(Binary.writeLShort(this.server.getConfig().getPort()));
        query.put(this.server.getConfig().getBindAddress().getBytes(StandardCharsets.UTF_8));
        query.put((byte) 0x00);
        return Arrays.copyOf(query.array(), query.position());
  }
}

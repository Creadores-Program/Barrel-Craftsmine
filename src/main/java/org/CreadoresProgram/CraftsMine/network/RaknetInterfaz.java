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
  public void sendPacket(String identifier, MCDPacket packet, boolean immediate){
    if(isDebug()){
        this.server.getLogger().debug("|Craftsman size| Send Datapack "+packet.getClass().getSimpleName());
    }
    if(identifier == null || packet == null){
      return;
    }
    boolean overridedImmediate = immediate || packet.isShouldSendImmediate();
    packet.encode();
    byte[] buffer = packet.getBuffer();
    // COMPRESSION REVERTED (f97e056): the wrapInBatch() compression of
    // outgoing packets ≥512 bytes was a suspected cause of the 0.15.x
    // client getting stuck at "Locating Server" without disconnecting.
    // The compressed batch format itself is symmetric with the proxy's
    // own BatchP translator (verified by symmetry test:
    // [0xfe][0x06][int32 BE len][zlib-wrapped deflate([int32 pkLen][pk bytes]...)]),
    // and matches MySoft-0.15.x-1.21.x's OldBatchPacket format for
    // protocol < 90 (ZLIB_ENCODING_DEFLATE = RFC1950 zlib wrapper).
    // However, some 0.15.x client builds appear to mishandle server-sent
    // compressed batches during the login handshake — possibly an
    // off-by-one in their inflater's expected trailer handling, or a
    // frame-size limit smaller than the proxy assumes. Sending every
    // packet raw (as before f97e056) restores the pre-regression
    // behaviour. FullChunkData (~83KB) goes back to ~60 RakNet split
    // fragments, which is less efficient on lossy networks but
    // functional. Compression can be re-enabled per-packet once the
    // root cause of the client-side inflation failure is identified.
    EncapsulatedPacket encapsulated = new EncapsulatedPacket();
    encapsulated.buffer = Binary.appendBytes((byte)0xfe, buffer);
    // CRITICAL FIX (verified against PocketMine master-legacy
    // RakLibInterface::putPacket - the "#blameshoghi" line): every game
    // packet MUST be sent with reliability 3 (RELIABLE_ORDERED) on
    // ordering channel 0. The previous code sent channel-0 packets with
    // reliability 2 (RELIABLE, UNORDERED): on any packet loss the RakNet
    // layer redelivers late, so the 0.15.x client received e.g.
    // MoveEntity before the AddEntity of the same entity and crashed.
    // reliability 3 + orderChannel 0 makes the RakNet layer deliver the
    // encapsulated game packets to the client strictly in send order.
    encapsulated.reliability = 3;
    encapsulated.orderChannel = 0;
    encapsulated.orderIndex = 0; // reassigned by Session (channelIndex++)
    encapsulated.messageIndex = 0; // reassigned by Session (messageIndex++)
    handler.sendEncapsulated(identifier, encapsulated, (overridedImmediate ? RakNet.PRIORITY_IMMEDIATE : RakNet.PRIORITY_NORMAL));
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

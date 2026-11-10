package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.nukkitLib.utils.Zlib;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.BinaryStream;
import org.CreadoresProgram.CraftsMine.server.Server;
import org.CreadoresProgram.CraftsMine.network.protocol.Interface.Protocol;
import org.CreadoresProgram.CraftsMine.dragonetLib.proxy.utilities.Binary;
import java.io.IOException;

/**
 * Desempaqueta un BatchPacket 0.15.x recibido del cliente upstream.
 *
 * <p>El cliente 0.15.x NO envía los paquetes de juego sueltos: los agrupa
 * dentro de un BatchPacket (PID 0x09). El formato del BatchPacket es:</p>
 * <ol>
 *   <li>{@code int length} (big-endian) — tamaño del payload comprimido</li>
 *   <li>{@code byte[length] payload} — datos comprimidos con zlib</li>
 * </ol>
 *
 * <p>Dentro del payload descomprimido, los sub-paquetes se concatenan con
 * un prefijo de longitud:</p>
 * <ol>
 *   <li>{@code int pklen} (big-endian) — tamaño del sub-paquete</li>
 *   <li>{@code byte[pklen] pkData} — sub-paquete crudo (PID + cuerpo)</li>
 * </ol>
 *
 * <p>Cada sub-paquete se decodifica con Protocol.decode(byte[]) y se
 * reenvía a PacketTranslatorManager.translate() para su traducción al
 * formato v113 y envío al servidor MCPE 1.1.</p>
 *
 * <p>IMPORTANTE: el BatchPacket en sí nunca debe reenviarse al servidor
 * MCPE 1.1 tal cual. El servidor 1.1 espera paquetes individuales ya
 * desempaquetados. Sin este translator, los paquetes de juego del cliente
 * (chat, movimiento, interact, etc.) quedan atrapados dentro del batch y
 * nunca se traducen.</p>
 */
public class BatchP implements CraftsmanPacketTranslator{
  private static final int MAX_DECOMPRESSED_SIZE = 1024 * 1024 * 64;

  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.BatchPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.BatchPacket) pk;
    boolean isDebug = Server.getInstance().getConfig().isDebug();
    byte[] payload = packet.payload;
    if(payload == null || payload.length == 0){
      return;
    }
    if(isDebug){
      Server.getInstance().getLogger().debug("decode BatchPacket Craftsman size... (compressed=" + payload.length + ")");
    }
    byte[] decompressedPayload;
    try{
      decompressedPayload = Zlib.inflate(payload, MAX_DECOMPRESSED_SIZE);
    }catch(IOException | RuntimeException ex){
      Server.getInstance().getLogger().warn("Failed to inflate BatchPacket payload (" + payload.length + " bytes): " + ex.getMessage());
      return;
    }
    if(decompressedPayload == null || decompressedPayload.length == 0){
      return;
    }
    int totalSize = decompressedPayload.length;
    BinaryStream dataReader = new BinaryStream(decompressedPayload);
    int offset = 0;
    while(offset + 4 <= totalSize){
      int pklen = dataReader.getInt();
      offset += 4;
      if(pklen <= 0 || offset + pklen > totalSize){
        break;
      }
      byte[] pkData = dataReader.get(pklen);
      if(pkData == null || pkData.length == 0){
        break;
      }
      offset += pklen;
      MCDPacket subPacket;
      try{
        subPacket = Protocol.decode(pkData);
      }catch(Throwable t){
        if(isDebug){
          Server.getInstance().getLogger().debug("Failed to decode sub-packet in batch: " + t.getMessage());
        }
        continue;
      }
      if(subPacket == null){
        continue;
      }
      if(isDebug){
        Server.getInstance().getLogger().debug("Process " + subPacket.getClass().getSimpleName() + " 0x" + Binary.bytesToHexString(subPacket.getBuffer()));
      }
      player.getPacketTranslatorManager().translate(subPacket);
    }
  }
}

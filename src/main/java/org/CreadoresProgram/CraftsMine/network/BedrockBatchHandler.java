package org.CreadoresProgram.CraftsMine.network;
import org.CreadoresProgram.CraftsMine.player.Player;
import org.CreadoresProgram.CraftsMine.server.Server;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import io.netty.buffer.ByteBuf;
import java.util.Collection;

/**
 * Receives the decoded batch of Bedrock packets from the downstream
 * {@link BedrockSession} and forwards each one to the per-player
 * {@link org.CreadoresProgram.CraftsMine.network.translator.PacketTranslatorManager}.
 *
 * <p>Replaces the previous CloudburstMC-style {@code BedrockPacketHandler}
 * implementation that returned {@code PacketSignal.HANDLED/UNHANDLED}.
 * NukkitX's API is different: a {@link BatchHandler} receives the whole
 * decoded batch in one call and does not return a signal - every packet
 * is implicitly "handled" because we always dispatch it to the translator.</p>
 *
 * <p>Packets that the translator registry has no mapping for are silently
 * ignored (the registry's {@code translate} method is a no-op when no
 * translator is registered for the packet class).</p>
 */
public class BedrockBatchHandler implements BatchHandler {
  private final Player player;
  public BedrockBatchHandler(Player player) {
        this.player = player;
  }
  @Override
  public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
    boolean isDebug = Server.getInstance().getConfig().isDebug();
    for (BedrockPacket packet : packets) {
      try {
        if (isDebug) {
          Server.getInstance().getLogger().debug("|MC Bedrock| Recive Datapack " + packet.getClass().getSimpleName());
        }
        player.getPacketTranslatorManager().translate(packet);
      } catch (Exception ex) {
        Server.getInstance().getLogger().error("Datapacket translate error: ", ex);
      }
    }
  }
}

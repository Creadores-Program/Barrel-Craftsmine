package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;

/** v113 DisconnectPacket (String kickMessage) → 0.15.x DisconnectPacket (String message). */
public class DisconnectPack implements BedrockPacketTranslator{
  // CRITICAL: Disconnect must reach the client immediately - if the
  // per-player bedrockTranslateQueue has been shut down (which is
  // exactly what happens during Player.disconnect()), queueing this
  // translator would silently drop the disconnect message via
  // RejectedExecutionException. immediate()=true runs it inline on
  // the Netty thread so the message always reaches the client.
  @Override
  public boolean immediate(){ return true; }
  @Override
  public void translate(BedrockPacket pk, Player player){
    DisconnectPacket packet = (DisconnectPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket();
    npk.message = packet.getKickMessage() == null ? "Disconnected" : packet.getKickMessage();
    player.sendDataCraftsman(npk);
  }
}

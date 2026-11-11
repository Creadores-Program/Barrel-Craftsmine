package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;

/** v113 DisconnectPacket (String kickMessage) → 0.15.x DisconnectPacket (String message). */
public class DisconnectPack implements BedrockPacketTranslator{
  // immediate() removed: this translator sends packets to the 0.15.x
  // client, so it MUST go through the per-player ordered FIFO to
  // preserve packet ordering (inline execution on the Netty thread let
  // it overtake earlier queued packets).
  @Override
  public void translate(BedrockPacket pk, Player player){
    DisconnectPacket packet = (DisconnectPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.DisconnectPacket();
    npk.message = packet.getKickMessage() == null ? "Disconnected" : packet.getKickMessage();
    player.sendDataCraftsman(npk);
  }
}

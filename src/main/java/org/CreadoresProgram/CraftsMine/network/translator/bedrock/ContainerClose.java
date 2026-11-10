package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;

/** v113 ContainerClosePacket (byte id) → 0.15.x WindowClosePacket (int windowid). */
public class ContainerClose implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ContainerClosePacket packet = (ContainerClosePacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.WindowClosePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.WindowClosePacket();
    npk.windowid = packet.getId();
    player.sendDataCraftsman(npk);
  }
}

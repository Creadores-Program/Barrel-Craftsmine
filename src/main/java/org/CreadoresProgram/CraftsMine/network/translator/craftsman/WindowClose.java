package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.ContainerClosePacket;

/** 0.15.x WindowClosePacket (int windowid) → v113 ContainerClosePacket (byte id). */
public class WindowClose implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.WindowClosePacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.WindowClosePacket) pk;
    ContainerClosePacket npk = new ContainerClosePacket();
    npk.setId((byte) packet.windowid);
    player.getBedrockClientSession().sendPacket(npk);
  }
}

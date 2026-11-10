package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.SetHealthPacket;

/** 0.15.x SetHealthPacket (int health) → v113 SetHealthPacket (int health). */
public class SetHealth implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.SetHealthPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.SetHealthPacket) pk;
    SetHealthPacket npk = new SetHealthPacket();
    npk.setHealth(packet.health);
    player.getBedrockClientSession().sendPacket(npk);
  }
}

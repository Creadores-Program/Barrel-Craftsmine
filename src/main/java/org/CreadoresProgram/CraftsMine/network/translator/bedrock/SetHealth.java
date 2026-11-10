package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetHealthPacket;

/** v113 SetHealthPacket (int health) → 0.15.x SetHealthPacket (int health). */
public class SetHealth implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetHealthPacket packet = (SetHealthPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetHealthPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetHealthPacket();
    npk.health = packet.getHealth();
    player.sendDataCraftsman(npk);
  }
}

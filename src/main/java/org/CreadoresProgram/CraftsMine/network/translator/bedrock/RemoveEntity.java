package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.RemoveEntityPacket;

/** v113 RemoveEntityPacket (long uniqueEntityId) → 0.15.x RemoveEntityPacket (long eid). */
public class RemoveEntity implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    RemoveEntityPacket packet = (RemoveEntityPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.RemoveEntityPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.RemoveEntityPacket();
    npk.eid = packet.getUniqueEntityId();
    player.sendDataCraftsman(npk);
  }
}

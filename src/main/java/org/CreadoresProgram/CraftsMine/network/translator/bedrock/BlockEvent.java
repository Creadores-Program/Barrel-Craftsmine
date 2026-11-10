package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.BlockEventPacket;
import com.nukkitx.math.vector.Vector3i;

/**
 * v113 BlockEventPacket (Vector3i blockPosition, int eventType, int
 * eventData) → 0.15.x BlockEventPacket (int x,y,z, int case1, int case2).
 */
public class BlockEvent implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    BlockEventPacket packet = (BlockEventPacket) pk;
    Vector3i pos = packet.getBlockPosition();
    if (pos == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.BlockEventPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.BlockEventPacket();
    npk.x = pos.getX();
    npk.y = pos.getY();
    npk.z = pos.getZ();
    npk.case1 = packet.getEventType();
    npk.case2 = packet.getEventData();
    player.sendDataCraftsman(npk);
  }
}

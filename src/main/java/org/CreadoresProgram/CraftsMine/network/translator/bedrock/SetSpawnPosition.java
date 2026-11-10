package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetSpawnPositionPacket;
import com.nukkitx.math.vector.Vector3i;

/** v113 SetSpawnPositionPacket (Vector3i blockPosition) → 0.15.x SetSpawnPositionPacket (int x,y,z). */
public class SetSpawnPosition implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetSpawnPositionPacket packet = (SetSpawnPositionPacket) pk;
    Vector3i pos = packet.getBlockPosition();
    if (pos == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.SetSpawnPositionPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetSpawnPositionPacket();
    npk.x = pos.getX();
    npk.y = pos.getY();
    npk.z = pos.getZ();
    player.sendDataCraftsman(npk);
  }
}

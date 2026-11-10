package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.math.vector.Vector3f;

/** v113 RespawnPacket (Vector3f position) → 0.15.x RespawnPacket (float x,y,z). */
public class Respawn implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    RespawnPacket packet = (RespawnPacket) pk;
    Vector3f pos = packet.getPosition();
    if (pos == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.RespawnPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.RespawnPacket();
    npk.x = pos.getX();
    npk.y = pos.getY();
    npk.z = pos.getZ();
    player.sendDataCraftsman(npk);
  }
}

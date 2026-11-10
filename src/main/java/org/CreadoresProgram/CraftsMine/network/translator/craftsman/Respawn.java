package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.RespawnPacket;
import com.nukkitx.math.vector.Vector3f;

/** 0.15.x RespawnPacket (float x,y,z) → v113 RespawnPacket (Vector3f). */
public class Respawn implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.RespawnPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.RespawnPacket) pk;
    RespawnPacket npk = new RespawnPacket();
    npk.setPosition(Vector3f.from(packet.x, packet.y, packet.z));
    player.getBedrockClientSession().sendPacket(npk);
  }
}

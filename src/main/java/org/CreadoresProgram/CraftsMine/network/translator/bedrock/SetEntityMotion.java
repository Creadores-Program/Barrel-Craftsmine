package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityMotionPacket;
import com.nukkitx.math.vector.Vector3f;

/**
 * v113 SetEntityMotionPacket (single entity motion) → 0.15.x
 * SetEntityMotionPacket (Entry[] entities with one Entry).
 */
public class SetEntityMotion implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    SetEntityMotionPacket packet = (SetEntityMotionPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.SetEntityMotionPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityMotionPacket();
    Vector3f mot = packet.getMotion();
    if (mot == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.SetEntityMotionPacket.Entry entry =
        new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityMotionPacket.Entry(
            packet.getRuntimeEntityId(), mot.getX(), mot.getY(), mot.getZ());
    npk.entities = new org.CreadoresProgram.CraftsMine.network.protocol.SetEntityMotionPacket.Entry[]{entry};
    player.sendDataCraftsman(npk);
  }
}

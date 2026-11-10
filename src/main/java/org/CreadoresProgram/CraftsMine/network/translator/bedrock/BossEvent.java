package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.BossEventPacket;

/**
 * v113 BossEventPacket — 0.15.x has no equivalent. The packet is silently
 * dropped; the 0.15.x client will simply not see boss bars. This is
 * non-critical for gameplay.
 */
public class BossEvent implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    // No 0.15.x equivalent; drop.
    BossEventPacket packet = (BossEventPacket) pk;
    if (packet == null) return;
  }
}

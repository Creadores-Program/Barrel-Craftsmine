package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;

/**
 * v113 AnimatePacket (Action enum, runtimeEntityId, rowingTime) → 0.15.x
 * AnimatePacket (int action, long eid).
 *
 * <p>v113 Action enum ordinals align with 0.15.x action ints:
 * NO_ACTION=0, SWING_ARM=1 (the most common - melee attack swing),
 * WAKE_UP=2, CRITICAL_HIT=3, MAGIC_CRITICAL_HIT=4, ROW_RIGHT=5, ROW_LEFT=6.</p>
 *
 * <p>0.15.x has no rowingTime field; it is dropped.</p>
 */
public class Animate implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AnimatePacket packet = (AnimatePacket) pk;
    if (packet.getAction() == null) return;
    org.CreadoresProgram.CraftsMine.network.protocol.AnimatePacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AnimatePacket();
    npk.action = packet.getAction().ordinal();
    npk.eid = packet.getRuntimeEntityId();
    player.sendDataCraftsman(npk);
  }
}

package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.AnimatePacket;

/** 0.15.x AnimatePacket (int action, long eid) → v113 AnimatePacket. */
public class Animate implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.AnimatePacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.AnimatePacket) pk;
    AnimatePacket npk = new AnimatePacket();
    AnimatePacket.Action[] actions = AnimatePacket.Action.values();
    int ordinal = Math.max(0, Math.min(actions.length - 1, packet.action));
    npk.setAction(actions[ordinal]);
    npk.setRuntimeEntityId(packet.eid);
    player.getBedrockClientSession().sendPacket(npk);
  }
}

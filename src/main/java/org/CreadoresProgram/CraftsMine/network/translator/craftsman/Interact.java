package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.InteractPacket;

/**
 * 0.15.x InteractPacket → v113 InteractPacket.
 *
 * <p>0.15.x ACTION_RIGHT_CLICK(1) → INTERACT, ACTION_LEFT_CLICK(2) → DAMAGE
 * (the PvP attack), ACTION_VEHICLE_EXIT(3) → LEAVE_VEHICLE.</p>
 */
public class Interact implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket) pk;
    InteractPacket npk = new InteractPacket();
    switch(packet.action){
      case org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_RIGHT_CLICK:
        npk.setAction(InteractPacket.Action.INTERACT);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_LEFT_CLICK:
        npk.setAction(InteractPacket.Action.DAMAGE);
        break;
      case org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_VEHICLE_EXIT:
        npk.setAction(InteractPacket.Action.LEAVE_VEHICLE);
        break;
      default: return;
    }
    npk.setRuntimeEntityId(packet.target);
    player.getBedrockClientSession().sendPacket(npk);
  }
}

package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.InteractPacket;

/**
 * v113 InteractPacket → 0.15.x InteractPacket.
 *
 * <p>v113 Action enum → 0.15.x byte action mapping:</p>
 * <ul>
 *   <li>INTERACT (1) → ACTION_RIGHT_CLICK (1)</li>
 *   <li>DAMAGE (2) → ACTION_LEFT_CLICK (2) — this is the PvP attack</li>
 *   <li>LEAVE_VEHICLE (3) → ACTION_VEHICLE_EXIT (3)</li>
 *   <li>others → dropped (no 0.15.x equivalent)</li>
 * </ul>
 */
public class Interact implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    InteractPacket packet = (InteractPacket) pk;
    byte action;
    if (packet.getAction() == null) return;
    switch(packet.getAction()){
      case INTERACT:       action = org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_RIGHT_CLICK; break;
      case DAMAGE:         action = org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_LEFT_CLICK; break;
      case LEAVE_VEHICLE:  action = org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket.ACTION_VEHICLE_EXIT; break;
      default: return; // NONE, HOVER, NPC_OPEN, OPEN_INVENTORY — no 0.15.x equivalent
    }
    org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.InteractPacket();
    npk.action = action;
    npk.target = packet.getRuntimeEntityId();
    player.sendDataCraftsman(npk);
  }
}

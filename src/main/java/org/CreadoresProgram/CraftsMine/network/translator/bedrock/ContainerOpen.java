package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerOpenPacket;
import com.nukkitx.math.vector.Vector3i;

/**
 * v113 ContainerOpenPacket (byte id, ContainerType type, Vector3i
 * blockPosition, long uniqueEntityId) → 0.15.x WindowOpenPacket (byte
 * windowid, byte type, int slots, int x/y/z, long entityId).
 *
 * <p>0.15.x needs a `slots` count field that v113 does not provide.
 * We use a per-type default slot count (chest=27, dispenser=9, etc.).
 * The type enum ordinal in v113 maps to the 0.15.x byte type.</p>
 */
public class ContainerOpen implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ContainerOpenPacket packet = (ContainerOpenPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.WindowOpenPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.WindowOpenPacket();
    npk.windowid = packet.getId();
    npk.type = (byte) (packet.getType() == null ? 0 : packet.getType().ordinal());
    // Best-effort slot count by container type ordinal. These match the
    // Nukkit 0.15.x defaults for the common container types.
    switch(npk.type){
      case 0: npk.slots = 27; break; // CHEST
      case 1: npk.slots = 9;  break; // DISPENSER
      case 2: npk.slots = 9;  break; // WORKBENCH
      case 3: npk.slots = 9;  break; // FURNACE
      case 4: npk.slots = 5;  break; // ENCHANT_TABLE
      case 5: npk.slots = 3;  break; // BREWING_STAND
      default: npk.slots = 9; break;
    }
    Vector3i pos = packet.getBlockPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    player.sendDataCraftsman(npk);
  }
}

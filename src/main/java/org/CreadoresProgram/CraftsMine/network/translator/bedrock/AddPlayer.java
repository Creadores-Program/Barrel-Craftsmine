package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddPlayerPacket;
import com.nukkitx.math.vector.Vector3f;
import org.CreadoresProgram.CraftsMine.inventory.Item;
import java.util.HashMap;

/**
 * Translates a v113 AddPlayerPacket (another player spawns into view) into
 * the 0.15.x AddPlayerPacket and forwards it to the upstream client.
 *
 * <p>v113 rotation is a Vector3f (x=pitch, y=yaw, z=headYaw). 0.15.x
 * serializes yaw twice (yaw and headYaw both equal to yaw) per the
 * AddPlayer encode convention.</p>
 *
 * <p>ItemData hand → Item conversion: only id/damage/count are carried
 * over; NBT tag is dropped because the 0.15.x Item constructor expects a
 * NamedTag compound that would need a separate NbtMap → NamedTag
 * translation step.</p>
 */
public class AddPlayer implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AddPlayerPacket packet = (AddPlayerPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.AddPlayerPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddPlayerPacket();
    npk.uuid = packet.getUuid();
    npk.username = packet.getUsername() == null ? "" : packet.getUsername();
    npk.eid = packet.getRuntimeEntityId();
    Vector3f pos = packet.getPosition();
    if (pos != null) {
      npk.x = pos.getX();
      npk.y = pos.getY();
      npk.z = pos.getZ();
    }
    Vector3f mot = packet.getMotion();
    if (mot != null) {
      npk.speedX = mot.getX();
      npk.speedY = mot.getY();
      npk.speedZ = mot.getZ();
    }
    Vector3f rot = packet.getRotation();
    if (rot != null) {
      npk.pitch = rot.getX();
      npk.yaw = rot.getY();
    }
    // ItemData hand → 0.15.x Item (id, damage, count). NBT dropped.
    com.nukkitx.protocol.bedrock.data.inventory.ItemData hand = packet.getHand();
    if (hand != null && hand.getId() > 0) {
      npk.item = new Item(hand.getId(), hand.getDamage(), hand.getCount(), new byte[0]);
    } else {
      npk.item = new Item(0, 0, 0, new byte[0]);
    }
    npk.metadata = new HashMap<>();
    player.sendDataCraftsman(npk);
    // Track the eid as known to the 0.15.x client (see Player.knownEntities).
    player.getKnownEntities().add(packet.getRuntimeEntityId());
  }
}

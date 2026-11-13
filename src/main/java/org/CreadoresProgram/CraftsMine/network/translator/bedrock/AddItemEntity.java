package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddItemEntityPacket;
import com.nukkitx.math.vector.Vector3f;
import org.CreadoresProgram.CraftsMine.inventory.Item;

/** v113 AddItemEntityPacket (item dropped on ground) → 0.15.x AddItemEntityPacket. */
public class AddItemEntity implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    AddItemEntityPacket packet = (AddItemEntityPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.AddItemEntityPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.AddItemEntityPacket();
    npk.eid = packet.getRuntimeEntityId();
    com.nukkitx.protocol.bedrock.data.inventory.ItemData item = packet.getItemInHand();
    if (item != null) {
      npk.item = new Item(item.getId(), item.getDamage(), item.getCount(), new byte[0]);
    } else {
      npk.item = new Item(0, 0, 0, new byte[0]);
    }
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
    player.sendDataCraftsman(npk);
    // Track the eid as known to the 0.15.x client (see Player.knownEntities).
    player.getKnownEntities().add(packet.getRuntimeEntityId());
  }
}

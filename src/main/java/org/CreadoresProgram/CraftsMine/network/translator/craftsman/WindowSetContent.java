package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.ContainerSetContentPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import java.util.ArrayList;
import java.util.List;

/**
 * 0.15.x WindowSetContentPacket → v113 ContainerSetContentPacket.
 */
public class WindowSetContent implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.WindowSetContentPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.WindowSetContentPacket) pk;
    ContainerSetContentPacket npk = new ContainerSetContentPacket();
    npk.setWindowId(packet.windowid);
    npk.setEntityRuntimeId(player.getRuntimeEntityId());
    List<ItemData> items = new ArrayList<>();
    if (packet.slots != null) {
      for (org.CreadoresProgram.CraftsMine.inventory.Item slot : packet.slots) {
        if (slot == null || slot.realId <= 0) {
          items.add(ItemData.AIR);
        } else {
          items.add(ItemData.builder().id(slot.realId).damage(slot.realData).count(((Integer) slot.itemIns[2])).build());
        }
      }
    }
    // items is a final List<ItemData>; mutate in place via getItems().
    npk.getItems().addAll(items);
    npk.setHotbar(packet.hotbar == null ? new int[0] : packet.hotbar);
    player.getBedrockClientSession().sendPacket(npk);
  }
}

package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerSetContentPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import org.CreadoresProgram.CraftsMine.inventory.Item;

/**
 * v113 ContainerSetContentPacket (int windowId, runtimeEntityId,
 * List&lt;ItemData&gt; items, int[] hotbar) → 0.15.x WindowSetContentPacket
 * (int windowid, Item[] slots, int[] hotbar).
 *
 * <p>Hotbar is only meaningful for SPECIAL_INVENTORY (windowid=0).
 * Item NBT is dropped (id/damage/count only) for each slot.</p>
 */
public class ContainerSetContent implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ContainerSetContentPacket packet = (ContainerSetContentPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.WindowSetContentPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.WindowSetContentPacket();
    npk.windowid = packet.getWindowId();
    java.util.List<ItemData> items = packet.getItems();
    if (items == null) items = java.util.Collections.emptyList();
    npk.slots = new Item[items.size()];
    for (int i = 0; i < items.size(); i++) {
      ItemData d = items.get(i);
      // CRITICAL: BinaryStream.putSlot() accesses itemIns[3].length without
      // null-checking. Passing null as the NBT parameter causes NPE at encode
      // time. Always pass an empty byte[] instead of null.
      if (d == null || d.getId() <= 0) {
        npk.slots[i] = new Item(0, 0, 0, new byte[0]);
      } else {
        npk.slots[i] = new Item(d.getId(), d.getDamage(), d.getCount(), new byte[0]);
      }
    }
    int[] hotbar = packet.getHotbar();
    npk.hotbar = hotbar == null ? new int[0] : hotbar;
    player.sendDataCraftsman(npk);
  }
}

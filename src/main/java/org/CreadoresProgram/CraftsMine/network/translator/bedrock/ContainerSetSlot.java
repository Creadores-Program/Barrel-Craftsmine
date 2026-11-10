package org.CreadoresProgram.CraftsMine.network.translator.bedrock;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.BedrockPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ContainerSetSlotPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import org.CreadoresProgram.CraftsMine.inventory.Item;

/**
 * v113 ContainerSetSlotPacket (short windowId, int slot, int hotbarSlot,
 * ItemData item, short selectSlot) → 0.15.x WindowSetSlotPacket (int
 * windowid, int slot, int hotbarSlot, Item item).
 *
 * <p>v113 selectSlot is dropped (no 0.15.x equivalent). Item NBT dropped.</p>
 */
public class ContainerSetSlot implements BedrockPacketTranslator{
  @Override
  public void translate(BedrockPacket pk, Player player){
    ContainerSetSlotPacket packet = (ContainerSetSlotPacket) pk;
    org.CreadoresProgram.CraftsMine.network.protocol.WindowSetSlotPacket npk =
        new org.CreadoresProgram.CraftsMine.network.protocol.WindowSetSlotPacket();
    npk.windowid = packet.getWindowId();
    npk.slot = packet.getSlot();
    npk.hotbarSlot = packet.getHotbarSlot();
    ItemData item = packet.getItem();
    if (item != null && item.getId() > 0) {
      npk.item = new Item(item.getId(), item.getDamage(), item.getCount(), new byte[0]);
    } else {
      npk.item = new Item(0, 0, 0, new byte[0]);
    }
    player.sendDataCraftsman(npk);
  }
}

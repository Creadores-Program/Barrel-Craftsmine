package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.ContainerSetSlotPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

/**
 * 0.15.x WindowSetSlotPacket → v113 ContainerSetSlotPacket.
 */
public class WindowSetSlot implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.WindowSetSlotPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.WindowSetSlotPacket) pk;
    ContainerSetSlotPacket npk = new ContainerSetSlotPacket();
    npk.setWindowId((short) packet.windowid);
    npk.setSlot(packet.slot);
    npk.setHotbarSlot(packet.hotbarSlot);
    org.CreadoresProgram.CraftsMine.inventory.Item item = packet.item;
    if (item != null && item.realId > 0) {
      npk.setItem(ItemData.builder().id(item.realId).damage(item.realData).count(((Integer) item.itemIns[2])).build());
    } else {
      npk.setItem(ItemData.AIR);
    }
    player.getBedrockClientSession().sendPacket(npk);
  }
}

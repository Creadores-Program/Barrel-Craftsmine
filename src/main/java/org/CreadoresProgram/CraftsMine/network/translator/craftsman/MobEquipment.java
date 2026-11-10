package org.CreadoresProgram.CraftsMine.network.translator.craftsman;
import org.CreadoresProgram.CraftsMine.network.protocol.base.MCDPacket;
import org.CreadoresProgram.CraftsMine.network.translator.interfaces.CraftsmanPacketTranslator;
import org.CreadoresProgram.CraftsMine.player.Player;
import com.nukkitx.protocol.bedrock.packet.MobEquipmentPacket;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;

/**
 * 0.15.x MobEquipmentPacket (eid, Item item, slot, selectedSlot) → v113
 * MobEquipmentPacket (runtimeEntityId, ItemData item, inventorySlot,
 * hotbarSlot, containerId).
 */
public class MobEquipment implements CraftsmanPacketTranslator {
  @Override
  public void translate(MCDPacket pk, Player player){
    org.CreadoresProgram.CraftsMine.network.protocol.MobEquipmentPacket packet =
        (org.CreadoresProgram.CraftsMine.network.protocol.MobEquipmentPacket) pk;
    MobEquipmentPacket npk = new MobEquipmentPacket();
    npk.setRuntimeEntityId(packet.eid);
    // 0.15.x Item → v113 ItemData (id, damage, count). NBT dropped.
    org.CreadoresProgram.CraftsMine.inventory.Item item = packet.item;
    if (item != null && item.realId > 0) {
      npk.setItem(ItemData.builder()
          .id(item.realId)
          .damage(item.realData)
          .count(((Integer) item.itemIns[2]))
          .build());
    } else {
      npk.setItem(ItemData.AIR);
    }
    npk.setInventorySlot(packet.slot);
    npk.setHotbarSlot(packet.selectedSlot);
    npk.setContainerId(0);
    player.getBedrockClientSession().sendPacket(npk);
  }
}
